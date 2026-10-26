/* CampusLoop frontend. All data comes from /api (shared database). */
const API='/api',TK='cl_token',LAST='cl_last';
const P={k:'#1b1b1b',g:'#6cc24a',d:'#7fd05a',T:'#3a8a3c',s:'#fff1e8',m:'#bfe6a6',e:'#e5557a',b:'#ffb3c1',l:'#b9f08a',p:'#f08a8a',q:'#c85a6a',f:'#f6a0b4',h:'#ffd1da',w:'#fff'};
const SP={
girl:["..gg........gg..","..gg..kkkk..gg..","....kkmmmmkk....","...kmmmmmmmmk...","..kmmmmmmmmmmk..","..kmmssssssmmk..","..kmssssssssmk..","..kmseesseesmk..","..kmssssbbssmk..","...kmsssssssmk..","....kkssssskk...","...kkllllllllkk.","..kllllllllllllk","..kddddddddddddk",".....ss....ss...",".....kk....kk..."],
flower:["...ffff...","..ffhhff..","..fhhhhf..","..ffhhff..","...ffff...","....T.....","..g.T.....","..ggT.....","...gT.gg..","....Tggg..","....T.gg..","....T.....","..pppppp..","..pqqqqp..","...pppp..."],
sprout:[".gg....gg.","gggg..gggg",".gggTTggg.","...gTTg...","....TT....","....TT....","..pppppp..","..pqqqqp..","...pppp..."],
heart:[".hh...hh.","hwhh.hhhh","hhhhhhhhh",".hhhhhhh.","..hhhhh..","...hhh...","....h...."],
mail:["kkkkkkkkkk","kfkhhhhkfk","kffkhhkffk","kfhfkkfhfk","kfhhffhhfk","kffffffffk","kkkkkkkkkk"],
smile:["...kkkk...",".kkllllkk.",".kllllllk.","kllkllkllk","kllkllkllk","kllllllllk","klkllllklk","kllkkkkllk",".kllllllk.","..kkkkkk.."]};
const sp=(n,s=6)=>{const r=SP[n],w=Math.max(...r.map(x=>x.length));let o='';r.forEach((row,y)=>[...row].forEach((c,x)=>{if(P[c])o+=`<rect x="${x}" y="${y}" width="1.03" height="1.03" fill="${P[c]}"/>`}));return `<svg viewBox="0 0 ${w} ${r.length}" width="${w*s}" height="${r.length*s}" shape-rendering="crispEdges" aria-hidden="true">${o}</svg>`};

const Q=["Pass it on — someone on campus needs it today.","Small things, big smiles.","Reuse today, bloom tomorrow.","Your done is someone's day one.","Plant a good deed, pick a happier campus."];
const TILES=[['All','🌈'],['Books','📚'],['Study Materials','📝'],['Electronics','🎧'],['Clothing','🥼'],['Stationery','✏️'],['Accessories','🎒'],['Hostel Essentials','🛏️'],['Other','📦']];
const CATS=TILES.slice(1),ICON=Object.fromEntries(TILES);
const DEPTS=['CSE','ECE','EEE','IT','Mechanical','Civil'],YEARS=['1st Year','2nd Year','3rd Year','4th Year'];
const SRT={new:(a,b)=>b.ts-a.ts,pl:(a,b)=>a.price-b.price,ph:(a,b)=>b.price-a.price,da:(a,b)=>a.days-b.days,dd:(a,b)=>b.days-a.days};
const KI={req:'📥',contact:'📞',ok:'🎉',no:'💬'};
const PRODUCT_PHOTOS={1:'https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=700&q=80',2:'https://images.unsplash.com/photo-1587145820266-a5951ee6f620?auto=format&fit=crop&w=700&q=80',3:'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=700&q=80',4:'https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?auto=format&fit=crop&w=700&q=80',5:'https://images.unsplash.com/photo-1517842645767-c639042777db?auto=format&fit=crop&w=700&q=80',6:'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=700&q=80',7:'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=700&q=80',8:'https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=700&q=80',9:'https://images.unsplash.com/photo-1583485088034-697b5bc54ccd?auto=format&fit=crop&w=700&q=80',10:'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=700&q=80'};

const $=s=>document.querySelector(s),esc=s=>String(s??'').replace(/[&<>"']/g,c=>'&#'+c.charCodeAt(0)+';');
let S=null;                       // server state for the signed-in student
let F={q:'',cat:'All',type:'',status:'',free:false,sort:'new'},TAB='list',EDIT=null,IMG='',CUR='home',MODE='login',CB=null,qi=0;

const isAdmin=()=>S.me.role==='admin';
const U=r=>S.users[r]||{name:'Unknown',reg:r,phone:'—',email:'—',dept:'',year:''};
const item=id=>S.items.find(i=>i.id==id);
const money=x=>x.price?'₹'+x.price:'FREE';
const unread=()=>S.notes.filter(n=>!n.read).length;
const bad=m=>toast(m,0);
const img=x=>(x.img||PRODUCT_PHOTOS[x.id])?`<img src="${x.img||PRODUCT_PHOTOS[x.id]}" alt="${esc(x.title)}" data-ic="${ICON[x.cat]||'📦'}" loading="lazy" onerror="imgFail(this)">`:`<span>${ICON[x.cat]||'📦'}</span>`;
function imgFail(el){el.onerror=null;const ic=el.dataset.ic||'📦',p=el.parentNode;el.remove();p.insertAdjacentHTML('beforeend',`<span>${ic}</span>`)}
const opts=(a,v)=>a.map(x=>`<option${x===v?' selected':''}>${x}</option>`).join('');

function toast(m,ok=1){const t=$('#toast');t.textContent=m;t.className=ok?'':'bad';t.style.display='block';clearTimeout(t.h);t.h=setTimeout(()=>t.style.display='none',3000)}
const modal=h=>$('#modal').innerHTML=`<div class=mb onclick="if(event.target==this)closeM()"><div class=mc><button class=x onclick="closeM()" aria-label=Close>✕</button>${h}</div></div>`;
const closeM=()=>$('#modal').innerHTML='';
function ask(msg,fn){CB=fn;modal(`<h2 class=pix style="font-size:18px">Are you sure?</h2><p style="margin:10px 0 16px">${msg}</p><button class="btn r" onclick="closeM();CB()">Yes, continue</button>`)}

/* ---------- server calls ---------- */
async function api(action,data={}){
  const r=await fetch(API,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({action,token:localStorage.getItem(TK)||'',...data})});
  const j=await r.json().catch(()=>({}));
  if(!r.ok){if(r.status===401&&action!=='login'&&action!=='signup')logoutLocal();throw new Error(j.error||'Something went wrong.')}
  return j;
}
async function refresh(){S=await api('state')}
async function run(action,data,msg){
  try{const j=await api(action,data);if(msg)toast(typeof msg==='function'?msg(j):msg);if(action!=='login'){await refresh()}render();return j}
  catch(e){toast(e.message,0)}
}

/* ---------- auth ---------- */
function auth(){const L=MODE=='login',last=localStorage.getItem(LAST)||'';
  return `<div class=auth><div class=lhero><div class=art>${sp('flower',8)}${sp('girl',11)}${sp('sprout',8)}</div>
  <h1 class=pix style="font-size:clamp(26px,4vw,40px)">CAMPUS<br><span style="color:var(--gr)">LOOP</span></h1>
  <p><b>Good finds. Better futures.</b><br>Donate, sell and discover second-hand treasures from your campus circle.</p></div>
  <form class=acard onsubmit="${L?'login':'signup'}(event);return false"><div class=circ>${sp('sprout',6)}</div><h2 class=pix>${L?'Log in':'Join CampusLoop'}</h2>
  <label>Registration number</label><input id=${L?'lr':'sr'} inputmode=numeric maxlength=10 placeholder="${L?'e.g. 2025501024':'202550 + 4 digits'}" required value="${esc(L?last:'')}">
  ${L?'':'<label>Full name</label><input id=sn placeholder="Your full name" required>'}
  <label>Password</label><input id=${L?'lp':'sp'} type=password placeholder="••••••••" required>
  ${L?'':'<label>Confirm password</label><input id=sc type=password placeholder="••••••••" required>'}
  <button class=btn>${L?'Log in':'Create account'}</button>
  <p class=sw>${L?'New here? <a onclick="setMode(\'signup\')">Create a new account</a>':'Already have an account? <a onclick="setMode(\'login\')">Log in.</a>'}</p></form></div>`}
const setMode=m=>{MODE=m;render()};
async function login(e){e.preventDefault();const reg=$('#lr').value.trim();
  try{const j=await api('login',{reg,password:$('#lp').value});localStorage.setItem(TK,j.token);localStorage.setItem(LAST,reg);CUR='home';await refresh();toast('Login successful ✓');render()}
  catch(err){toast(err.message,0);const f=e.target;f.classList.add('shake');setTimeout(()=>f.classList.remove('shake'),400)}}
async function signup(e){e.preventDefault();const reg=$('#sr').value.trim(),name=$('#sn').value.trim(),pw=$('#sp').value;
  if(pw!==$('#sc').value)return bad('Passwords do not match.');
  try{await api('signup',{reg,name,password:pw});localStorage.setItem(LAST,reg);MODE='login';toast('Account created ✓ Please log in');render()}
  catch(err){toast(err.message,0)}}
function logoutLocal(){localStorage.removeItem(TK);S=null;CUR='home';render()}
async function logout(){try{await api('logout')}catch(e){}logoutLocal();toast('Logged out. See you soon!')}

/* ---------- shell ---------- */
const NAV=[['home','sprout','Home'],['browse','mail','Explore Finds'],['requests','mail','Request Items'],['give','heart','Give & Share'],['dash','smile','My Dashboard'],['help','mail','Help Center'],['profile','flower','My Profile']];
function navTo(k){if(k==='give'){EDIT=null;IMG=''}if(k==='dash'&&unread())TAB='notes';go(k)}
function go(p){CUR=p;render();scrollTo(0,0)}
function shell(h){const m=S.me,un=unread();
  return `<div class=app><nav class=side><div class="logo pix">CAMPUS<b>LOOP</b></div><div class=who><b>${esc(m.name)}</b><br>${m.reg}${isAdmin()?' · admin':''}</div>
  ${NAV.map(([k,i,t])=>`<button class="nb ${CUR==k?'on':''}" onclick="navTo('${k}')">${sp(i,3)}${t}${k=='dash'&&un?`<span class=ct>${un}</span>`:''}</button>`).join('')}
  <div style="flex:1"></div><button class=nb onclick="logout()">⎋ Log out</button></nav><main class=main>${h}</main></div>`}

/* ---------- shared pieces ---------- */
function card(x){const w=(S.wish||[]).includes(x.id);
  return `<div class=card tabindex=0 onkeydown="if(event.key=='Enter')openItem(${x.id})" onclick="openItem(${x.id})">
  <div class=th>${img(x)}${x.price==0?'<em class=free>FREE</em>':''}${x.status!=='Available'?`<em class=st>${x.status}</em>`:''}
  <button class=heart title="Wishlist" aria-label="Wishlist" onclick="event.stopPropagation();wish(${x.id})">${w?'♥':'♡'}</button></div>
  <b>${esc(x.title)}</b><div class=pr>${money(x)}${x.price>0&&x.price<200?' <span class=tag>under ₹200</span>':''}</div>
  <small>${x.cat} · ${x.cond} · ${x.days} days used</small><small>Seller: ${esc(U(x.owner).name)}</small></div>`}
const wish=id=>run('wish',{id},j=>j.saved?'Saved for later ♥':'Removed from wishlist');
function notesHTML(n){const L=S.notes.slice().sort((a,b)=>b.ts-a.ts).slice(0,n);
  if(!L.length)return '<p class=empty>Every treasure deserves a second story.</p>';
  return (L.some(x=>!x.read)?`<div class=row style="margin:0 0 6px"><button class="btn sm r" onclick="readAll()">Mark all as read</button></div>`:'')+
    L.map(x=>`<div class="nt ${x.read?'':'unread'}"><span>${KI[x.k]||'🌱'} ${x.read?'':'🔴 '}${esc(x.msg)}<br><small>${new Date(x.ts).toLocaleString()}</small></span>${x.read?'':`<button class="btn sm" onclick="readN(${x.id})">Mark as read</button>`}</div>`).join('')}
const readN=id=>run('readNotes',{id});
const readAll=()=>run('readNotes',{});

/* ---------- items & requests ---------- */
function openItem(id){const x=item(id),m=S.me,own=x.owner===m.reg,o=U(x.owner),rq=S.reqs.some(r=>r.item===id&&r.from===m.reg&&r.status==='Pending');
  S.viewed=[id,...(S.viewed||[]).filter(v=>v!==id)].slice(0,6);api('viewed',{id}).catch(()=>{});
  modal(`<div class="th big">${img(x)}</div><h2 class=pix style="font-size:18px">${esc(x.title)}</h2>
  <div class=pr style="font-size:22px">${money(x)}</div><p style="margin:8px 0">${esc(x.desc)}</p>
  <p><b>${x.cat}</b> · ${x.cond} · Used ${x.days} days · <span class="tag ${x.status}">${x.status}</span></p>
  <small>Seller: ${esc(o.name)} (${o.reg})</small>
  ${own?'<p class=ci>This is your listing.</p>':`<div class=row><button class="btn r" onclick="contact(${id})">Contact seller</button><button class="btn g" ${rq||x.status!=='Available'?'disabled':''} onclick="request(${id})">${rq?'Request pending':x.status==='Available'?'Request item · '+money(x):'Not available'}</button><button class="btn sm r" onclick="reportItem(${id})">Report listing</button></div><div id=ci></div>`}`)}
function reportItem(id){const x=item(id);if(!x||x.owner===S.me.reg)return;
  modal(`<h2 class=pix style="font-size:18px">Report listing</h2><p style="margin:8px 0">Tell the admin why "${esc(x.title)}" should be reviewed. Reports are private.</p>
  <form onsubmit="return submitReport(event,${id})"><label>Reason</label><select id=reportReason required>${['Misleading or inaccurate listing','Prohibited or unsafe item','Inappropriate content','Suspected scam','Other'].map(x=>`<option>${x}</option>`).join('')}</select>
  <label>Details (optional)</label><textarea id=reportDetails maxlength=500 placeholder="Add helpful context for the admin"></textarea>
  <div class=row><button class=btn>Send private report</button></div></form>`)}
async function submitReport(e,id){e.preventDefault();try{await api('reportListing',{id,reason:$('#reportReason').value,details:$('#reportDetails').value});closeM();await refresh();if(CUR==='help')render();toast('Report sent privately to the admin.')}catch(err){toast(err.message,0)}return false}
function contact(id){const x=item(id),o=U(x.owner);
  $('#ci').innerHTML=`<div class=ci><b>${esc(o.name)}</b><br>Reg no: ${x.owner}<br>📱 ${esc(o.phone||'—')}<br>✉️ ${esc(o.email||'—')}<br>${esc(o.dept)}${o.year?' · '+esc(o.year):''}</div>
  <div class=ci>🌿 <b>Safe handoff:</b> meet on campus in a public place, in daylight, and bring a friend.</div>`}
function request(id){closeM();run('request',{id},'Notified to the owner ✓')}
const respond=(id,st)=>run('respond',{id,status:st},'Request '+st.toLowerCase()+'.');
function editItem(id){if(item(id).owner!==S.me.reg)return;EDIT=id;IMG=item(id).img||'';go('give')}
function delItem(id){const x=item(id);if(!x||x.owner!==S.me.reg)return;ask(`Delete "${esc(x.title)}" permanently?`,()=>run('deleteItem',{id},'Listing deleted.'))}
const setStatus=(id,s)=>run('setStatus',{id,status:s});

/* ---------- give / sell ---------- */
function pick(inp){const f=inp.files[0];if(!f)return;if(!f.type.startsWith('image/'))return bad('Please choose an image file.');
  const r=new FileReader();r.onload=()=>{const im=new Image();im.onload=()=>{const s=Math.min(1,480/Math.max(im.width,im.height)),c=document.createElement('canvas');
    c.width=Math.round(im.width*s);c.height=Math.round(im.height*s);c.getContext('2d').drawImage(im,0,0,c.width,c.height);IMG=c.toDataURL('image/jpeg',.7);
    $('#prev').innerHTML=`<img src="${IMG}" alt="preview" style="max-height:140px;border-radius:14px">`};im.src=r.result};r.readAsDataURL(f)}
function tp(){const d=$('#gt').value==='Donate';$('#gp').disabled=d;if(d)$('#gp').value=0}
async function publish(e){e.preventDefault();const g=id=>$('#'+id).value.trim(),don=g('gt')==='Donate',price=don?0:Number(g('gp'));
  if(!g('gn')||!g('gd'))return bad('Please fill in title and description.');
  if(!don&&!(price>0))return bad('Enter a price above ₹0, or choose Donate.');
  if(g('gu')===''||!(Number(g('gu'))>=0))return bad('Enter how many days the item was used.');
  const d={title:g('gn'),desc:g('gd'),cat:g('gcat'),cond:g('gcond'),type:don?'Donate':'Sell',price,days:Number(g('gu')),phone:g('gc'),dept:g('gdep'),img:IMG};
  if(EDIT)d.id=EDIT;
  try{await api('publish',d);toast(EDIT?'Listing updated ✓':'Your treasure is live! 🌱');EDIT=null;TAB='list';await refresh();go('dash')}
  catch(err){toast(err.message,0)}
  return false}

/* ---------- pages ---------- */
function drawG(){const q=F.q.toLowerCase(),L=S.items.filter(i=>(F.cat==='All'||i.cat===F.cat)&&(!F.type||i.type===F.type)&&(!F.status||i.status===F.status)&&(!F.free||i.price==0)&&
    (!q||(i.title+' '+i.desc+' '+i.cat+' '+U(i.owner).name).toLowerCase().includes(q))).sort(SRT[F.sort]);
  $('#grid').innerHTML=L.length?L.map(card).join(''):'<p class=empty style="grid-column:1/-1">No listings match those filters. Try changing your search.</p>'}
function browse(){const L=S.items.filter(i=>i.price<200&&i.status==='Available'&&i.price>=0).slice(0,6);
  return `<section class=hero><div><h1 class=pix>FIND YOUR NEXT<br><span>FAVOURITE THING</span></h1><p style="margin-top:8px">Good finds from students around you.</p></div><div class=art>${sp('flower',7)}${sp('sprout',7)}</div></section>
  <div class="bar"><input placeholder="🔎 Search name, description, category or seller" value="${esc(F.q)}" oninput="F.q=this.value;drawG()" aria-label=Search>
  <select onchange="F.type=this.value;drawG()" aria-label="Listing type">${[['','Donated & paid'],['Donate','Donated'],['Sell','Paid']].map(([v,t])=>`<option value="${v}" ${F.type===v?'selected':''}>${t}</option>`).join('')}</select>
  <select onchange="F.status=this.value;drawG()" aria-label="Availability">${[['','All statuses'],['Available','Available'],['Reserved','Reserved'],['Completed','Completed']].map(([v,t])=>`<option value="${v}" ${F.status===v?'selected':''}>${t}</option>`).join('')}</select>
  <label class=chk><input type=checkbox ${F.free?'checked':''} onchange="F.free=this.checked;drawG()">Free only</label>
  <select onchange="F.sort=this.value;drawG()" aria-label=Sort>${[['new','Sort: Newest'],['pl','Price: low to high'],['ph','Price: high to low'],['da','Days used: fewest'],['dd','Days used: most']].map(([v,t])=>`<option value="${v}" ${F.sort===v?'selected':''}>${t}</option>`).join('')}</select></div>
  <div class=tiles>${TILES.map(([c,e])=>`<button class="tile ${F.cat==c?'on':''}" onclick="F.cat='${c}';render()">${e} ${c}</button>`).join('')}</div>
  <div class=grid id=grid></div>
  <div class="pk"><h2 class=pix>Free &amp; under ₹200</h2><div class=grid>${L.map(card).join('')||'<p class=empty>Nothing here yet.</p>'}</div></div>`}
function give(){const e=EDIT?item(EDIT):null,m=S.me,v=(k)=>esc(e?e[k]:'');
  return `<section class=hero><div><h1 class=pix>GIVE IT<br><span>A NEW CHAPTER</span></h1><p style="margin-top:8px">${e?'Editing your listing.':'Donate for free or sell to a fellow student.'}</p></div><div class=art>${sp('heart',8)}${sp('smile',7)}</div></section>
  <form class="panel fg" onsubmit="return publish(event)">
  <div class=f><label>Upload item image</label><input type=file accept="image/*" onchange="pick(this)"><div id=prev style="margin-top:8px">${IMG?`<img src="${IMG}" alt="preview" style="max-height:140px;border-radius:14px">`:''}</div></div>
  <div><label>Item title</label><input id=gn value="${v('title')}"></div>
  <div><label>Category</label><select id=gcat>${opts(CATS.map(c=>c[0]),e?e.cat:'Books')}</select></div>
  <div class=f><label>Description</label><textarea id=gd rows=3>${v('desc')}</textarea></div>
  <div><label>Condition</label><select id=gcond>${opts(['New','Like New','Good','Fair'],e?e.cond:'Good')}</select></div>
  <div><label>Listing type</label><select id=gt onchange="tp()">${opts(['Donate','Sell'],e?e.type:'Donate')}</select></div>
  <div><label>Price (₹)</label><input id=gp type=number min=0 value="${e?e.price:0}" ${!e||e.type==='Donate'?'disabled':''}></div>
  <div><label>Days used</label><input id=gu type=number min=0 value="${e?e.days:''}"></div>
  <div><label>Contact number</label><input id=gc value="${esc(e?e.phone||m.phone:m.phone)}"></div>
  <div><label>Department</label><select id=gdep>${opts(DEPTS,e?e.dept:m.dept)}</select></div>
  <div class="f" style="display:flex;gap:10px;flex-wrap:wrap;margin-top:16px"><button class=btn>${e?'Save changes':'Publish my treasure'}</button>${e?`<button type=button class="btn r" onclick="EDIT=null;go('dash')">Cancel</button>`:''}</div></form>`}
function dash(){const m=S.me,mine=S.items.filter(i=>i.owner===m.reg),inc=S.reqs.filter(r=>r.to===m.reg),out=S.reqs.filter(r=>r.from===m.reg),wl=(S.wish||[]).map(item).filter(Boolean);
  const T=[['list','My listings'],['inc','Incoming requests'],['out','My requests'],['notes','Notifications'],['wish','Wishlist']];
  const empty='<p class=empty>Every treasure deserves a second story.</p>';
  const body={
    list:()=>mine.map(i=>`<div class=row2><span><b>${esc(i.title)}</b> · ${money(i)} · <span class="tag ${i.status}">${i.status}</span> · ${S.reqs.filter(r=>r.item===i.id&&r.status==='Pending').length} pending</span>
      <span class=row style="margin:0"><button class="btn sm r" onclick="editItem(${i.id})">Edit</button><button class="btn sm g" onclick="setStatus(${i.id},'${i.status==='Available'?'Reserved':'Available'}')">${i.status==='Available'?'Mark reserved':'Mark available'}</button><button class="btn sm red" onclick="delItem(${i.id})">Delete</button></span></div>`).join('')||empty,
    inc:()=>inc.map(r=>`<div class=row2><span><b>${esc(U(r.from).name)}</b> (${r.from}) wants <b>${esc(item(r.item)?.title||'(removed)')}</b> · ${r.date} · <span class="tag ${r.status}">${r.status}</span></span>
      <span class=row style="margin:0">${r.status==='Pending'?`<button class="btn sm g" onclick="respond(${r.id},'Accepted')">Accept</button><button class="btn sm red" onclick="respond(${r.id},'Declined')">Decline</button>`:''}${r.status==='Accepted'?`<button class="btn sm" onclick="respond(${r.id},'Completed')">Mark completed</button>`:''}</span></div>`).join('')||empty,
    out:()=>out.map(r=>`<div class=row2><span><b>${esc(item(r.item)?.title||'(removed)')}</b> · Owner: ${esc(U(r.to).name)} · ${r.date}</span><span class="tag ${r.status}">${r.status}</span></div>`).join('')||empty,
    notes:()=>notesHTML(99),
    wish:()=>wl.length?`<div class=grid>${wl.map(card).join('')}</div>`:empty};
  return `<section class=hero><div><h1 class=pix>MY<br><span>DASHBOARD</span></h1><p style="margin-top:8px">Your little corner of campus.</p></div><div class=art>${sp('smile',8)}</div></section>
  <div class=stats><div><b>${mine.length}</b>Listings</div><div><b>${inc.filter(r=>r.status==='Pending').length}</b>To answer</div><div><b>${out.length}</b>Requests sent</div></div>
  <div class=tiles>${T.map(([k,t])=>`<button class="tile ${TAB===k?'on':''}" onclick="TAB='${k}';render()">${t}</button>`).join('')}</div>
  <div class=pk>${body[TAB]()}</div>`}
function home(){const m=S.me,A=S.items,rec=[...A].sort(SRT.new).slice(0,4),rv=(S.viewed||[]).map(item).filter(Boolean);
  return `<section class=hero><div><p class=pix style="color:var(--gr)">Welcome, ${esc(m.name.split(' ')[0])} ✿</p><h1 class=pix>GOOD FINDS.<br><span>BETTER FUTURES.</span></h1>
  <p style="margin:10px 0 16px;max-width:400px">Donate, sell and discover second-hand treasures from your campus circle.</p>
  <button class=btn onclick="go('browse')">Explore finds</button> <button class="btn r" onclick="go('give')">Donate an item</button></div><div class=art>${sp('girl',9)}${sp('flower',6)}${sp('sprout',6)}</div></section>
  <div class=stats><div><b>${A.length}</b>Listings</div><div><b>${A.filter(i=>i.status==='Available').length}</b>Available</div><div><b>${A.filter(i=>i.price==0).length}</b>Free items</div></div>
  <div class=pk><h2 class=pix>Little updates from your campus circle</h2>${notesHTML(4)}</div>
  <div class=sec><h2 class=pix>Recently posted</h2><div class=grid>${rec.map(card).join('')}</div></div>
  ${rv.length?`<div class=sec><h2 class=pix>Recently viewed</h2><div class=grid>${rv.slice(0,4).map(card).join('')}</div></div>`:''}
  <div class=quote>${sp('heart',6)}<h2 class=pix id=qt style="font-size:16px;line-height:1.5">${Q[qi%Q.length]}</h2></div>
  <div class="pk" style="background:#fff"><h2 class=pix>How CampusLoop works</h2><p>1️⃣ <b>List</b> something you no longer need.<br>2️⃣ <b>Request</b> what you like. Owners accept or decline.<br>3️⃣ <b>Meet</b> safely on campus and hand it over.</p>
  <p><small>Safe handoff: public place, daylight, bring a friend.</small></p></div>`}
function profile(){const m=S.me;
  return `<section class=hero><div><h1 class=pix>MY<br><span>PROFILE</span></h1><p style="margin-top:8px">Registration number: <b>${m.reg}</b></p></div><div class=art>${sp('girl',8)}${sp('flower',6)}</div></section>
  <form class="panel" onsubmit="return saveProfile(event)"><div class=fg>
  <div><label>Full name</label><input id=pn value="${esc(m.name)}" required></div>
  <div><label>Registration number</label><input value="${m.reg}" disabled></div>
  <div><label>College year</label><select id=py>${opts(YEARS,m.year)}</select></div>
  <div><label>Department</label><select id=pd>${opts(DEPTS,m.dept)}</select></div>
  <div><label>Phone</label><input id=pp value="${esc(m.phone)}"></div>
  <div><label>Email</label><input id=pe type=email value="${esc(m.email)}"></div></div>
  <div class=row><button class=btn>Save changes</button></div>
  <p><small>Your registration number is your unique ID and can't be changed.</small></p></form>`}
function saveProfile(e){e.preventDefault();const n=$('#pn').value.trim();if(!n)return bad('Name is required.');
  run('updateProfile',{name:n,year:$('#py').value,dept:$('#pd').value,phone:$('#pp').value.trim(),email:$('#pe').value.trim()},'Profile saved ✿');return false}
function requests(){const m=S.me,av=S.items.filter(i=>i.owner!==m.reg&&i.status==='Available'),out=S.reqs.filter(r=>r.from===m.reg);
  const empty='<p class=empty>Nothing here yet. Every treasure deserves a second story.</p>';
  return `<section class=hero><div><h1 class=pix>REQUEST<br><span>AN ITEM</span></h1><p style="margin-top:8px">Pick something you need. The owner will be notified.</p></div><div class=art>${sp('sprout',7)}</div></section>
  <div class=pk><h2 class=pix>Available items (${av.length})</h2>${av.map(x=>`<div class=row2><span>${ICON[x.cat]||'📦'} <b>${esc(x.title)}</b> · ${money(x)} · ${x.cond} · Seller: ${esc(U(x.owner).name)}</span>
    <span class=row style="margin:0"><button class="btn sm" onclick="openItem(${x.id})">View</button><button class="btn sm g" onclick="request(${x.id})">Request</button></span></div>`).join('')||empty}</div>
  <div class="pk" style="background:#fff"><h2 class=pix>My requests (${out.length})</h2>${out.map(r=>`<div class=row2><span><b>${esc(item(r.item)?.title||'(removed)')}</b> · Owner: ${esc(U(r.to).name)} · ${r.date}</span><span class="tag ${r.status}">${r.status}</span></div>`).join('')||empty}</div>`}
function help(){const adm=isAdmin(),mine=(S.tickets||[]).slice().sort((a,b)=>b.ts-a.ts);
  return `<section class=hero><div><h1 class=pix>HELP <span>CENTER</span></h1><p style="margin-top:8px">Need a hand? Send a message to the CampusLoop admin.</p></div><div class=art>${sp('mail',7)}${sp('sprout',6)}</div></section>
  <div class=help-card><h2 class=pix>Contact the admin</h2><p style="margin-top:6px;color:#555">Send a message through the support form below.</p>
  <form onsubmit="submitTicket(event);return false" class=help-grid><div><label>Topic</label><select id=htopic required>${TOPICS_UI.map(t=>`<option>${t}</option>`).join('')}</select></div>
  <div><label>Contact email (optional)</label><input id=hemail type=email placeholder="you@example.com" value="${esc(S.me.email||'')}"></div>
  <div class=full><label>How can we help?</label><textarea id=hmsg rows=4 required maxlength=1500 placeholder="Describe the issue..."></textarea></div>
  <div class=full><button class=btn>Send support ticket</button></div></form></div>
  <div class=help-card><h2 class=pix>${adm?'Admin inbox':'Your support tickets'}</h2><p style="color:#555;margin-top:5px">${adm?'All student tickets. Reply, then mark the status.':'Check the status of messages you have sent.'}</p>
  ${mine.length?mine.map(t=>`<div class=ticket><div style="display:flex;gap:10px;justify-content:space-between;flex-wrap:wrap"><b>${esc(t.topic)}</b><span class="tag ${esc(t.status.replace(/\s/g,''))}">${esc(t.status)}</span></div>
    <small>${adm?'From '+esc(U(t.from).name)+' ('+t.from+') · ':''}${new Date(t.ts).toLocaleString()}</small><p style="margin-top:8px;white-space:pre-wrap">${esc(t.message)}</p>
    ${t.reply?`<div class=ci><b>Admin reply</b><br>${esc(t.reply)}</div>`:''}
    ${adm?`<label>Reply to student</label><textarea id="reply${t.id}" rows=2 placeholder="Write a reply">${esc(t.reply||'')}</textarea>
      <div class=row><button class="btn sm g" onclick="replyTicket(${t.id})">Save reply</button><button class="btn sm" onclick="statusTicket(${t.id},'In Progress')">In progress</button><button class="btn sm r" onclick="statusTicket(${t.id},'Resolved')">Mark resolved</button><button class="btn sm r" onclick="statusTicket(${t.id},'Open')">Reopen</button></div>`:''}</div>`).join(''):'<p class=empty>No support tickets yet.</p>'}</div>
  ${adm?`<div class=help-card><h2 class=pix>Private listing reports (${(S.reports||[]).length})</h2><p style="color:#555;margin-top:5px">Only admins can view reports and reporter details.</p>
  ${(S.reports||[]).length?S.reports.slice().sort((a,b)=>b.ts-a.ts).map(r=>`<div class=ticket><div style="display:flex;gap:10px;justify-content:space-between;flex-wrap:wrap"><b>${esc(r.reason)}</b><span class="tag ${esc(r.status)}">${esc(r.status)}</span></div>
  <small>${esc(item(r.item)?.title||r.title||'Removed listing')} · Reported by ${esc(U(r.from).name)} (${esc(r.from)}) · ${new Date(r.ts).toLocaleString()}</small>
  ${r.details?`<p style="margin-top:8px;white-space:pre-wrap">${esc(r.details)}</p>`:''}
  ${r.status==='Open'?`<div class=row><button class="btn sm g" onclick="setReportStatus(${r.id},'Reviewed')">Mark reviewed</button><button class="btn sm r" onclick="setReportStatus(${r.id},'Dismissed')">Dismiss report</button></div>`:''}</div>`).join(''):'<p class=empty>No listing reports yet.</p>'}</div>`:''}`}
const TOPICS_UI=['Account or login','Listing problem','Request problem','Report inappropriate content','Bug or suggestion','Other'];
function submitTicket(e){const message=$('#hmsg').value.trim();if(!message)return bad('Please enter a message.');
  run('ticket',{topic:$('#htopic').value,email:$('#hemail').value.trim(),message},'Support ticket submitted ✓')}
const replyTicket=id=>run('ticketReply',{id,reply:$('#reply'+id).value},'Reply saved ✓');
const statusTicket=(id,status)=>run('ticketStatus',{id,status},'Status updated ✓');
const setReportStatus=(id,status)=>run('setReportStatus',{id,status},'Report status updated.');
const PAGES={home,browse,requests,give,dash,help,profile};

/* ---------- render ---------- */
function render(){
  if(!S){$('#root').innerHTML=auth();return}
  if(!PAGES[CUR])CUR='home';
  $('#root').innerHTML=shell(PAGES[CUR]());
  if(CUR==='browse')drawG();
}
const bg=$('#bg');for(let i=0;i<30;i++){const s=document.createElement('i');s.style.cssText=`left:${Math.random()*100}%;top:${Math.random()*100}%;animation-delay:-${Math.random()*9}s`;bg.append(s)}
setInterval(()=>{const q=$('#qt');if(q)q.textContent=Q[++qi%Q.length]},5000);
addEventListener('hashchange',render);
(async()=>{
  if(localStorage.getItem(TK)){try{await refresh()}catch(e){S=null;localStorage.removeItem(TK)}}
  render();
})();
