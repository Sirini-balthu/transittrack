const $=(s,r=document)=>r.querySelector(s);
const esc=s=>String(s??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));

async function api(path,{method='GET',body}={}){
  let res;
  try{res=await fetch(path,{method,credentials:'same-origin',headers:body?{'Content-Type':'application/json'}:{},body:body?JSON.stringify(body):undefined});}
  catch(e){throw new Error('Cannot reach the server. Check that TransitTrack is running on port 8080.');}
  const isJson=(res.headers.get('content-type')||'').includes('json');
  const data=isJson?await res.json().catch(()=>null):await res.text();
  if(res.status===401&&!path.startsWith('/auth/login')){location.href='login.html';throw new Error('Please log in again.');}
  if(res.status===403)throw new Error('You do not have permission to do that.');
  if(!res.ok)throw new Error((data&&data.message)||'Request failed ('+res.status+')');
  return data;
}

function toast(msg,type=''){
  let box=$('#toasts');if(!box){box=document.createElement('div');box.id='toasts';box.setAttribute('role','status');document.body.appendChild(box);}
  const t=document.createElement('div');t.className='toast '+type;t.textContent=msg;box.appendChild(t);setTimeout(()=>t.remove(),3500);
}

function openModal(title,html){
  closeModal();
  const o=document.createElement('div');o.className='ov';o.id='modal';
  o.innerHTML='<div class="md" role="dialog" aria-modal="true" aria-label="'+esc(title)+'"><h2>'+esc(title)+'</h2>'+html+'</div>';
  o.addEventListener('mousedown',e=>{if(e.target===o)closeModal();});
  document.addEventListener('keydown',escClose);document.body.appendChild(o);
  const f=o.querySelector('input,select,textarea,button');if(f)f.focus();return o;
}
function escClose(e){if(e.key==='Escape')closeModal();}
function closeModal(){const m=$('#modal');if(m)m.remove();document.removeEventListener('keydown',escClose);}
function confirmBox(message,okText='Confirm',danger=false){
  return new Promise(res=>{
    const o=openModal('Please confirm','<p>'+esc(message)+'</p><div class="acts"><button class="btn sec" id="cn">Cancel</button><button class="btn '+(danger?'bad':'')+'" id="cy">'+esc(okText)+'</button></div>');
    o.querySelector('#cn').onclick=()=>{closeModal();res(false)};o.querySelector('#cy').onclick=()=>{closeModal();res(true)};
  });
}

const stopsOf=r=>String(r.stops||'').split(/[,;>\n]+/).map(s=>s.trim()).filter(Boolean);
const t12=t=>{if(!t)return'—';const[h,m]=String(t).split(':');return((+h%12)||12)+':'+m+(+h<12?' AM':' PM')};
const statusBadge=s=>({ON_TIME:'<span class="badge b-ok">On time</span>',DELAYED:'<span class="badge b-bad">Delayed</span>',APPROVED:'<span class="badge b-ok">Approved</span>',REJECTED:'<span class="badge b-bad">Rejected</span>',PENDING:'<span class="badge b-warn">Pending</span>',ACTIVE:'<span class="badge b-ok">Active</span>',MAINTENANCE:'<span class="badge b-warn">Maintenance</span>'}[String(s).toUpperCase()]||'<span class="badge b-warn">'+esc(s)+'</span>');
const skeleton=n=>Array.from({length:n},()=>'<div class="sk"></div>').join('');
const debounce=(f,ms=250)=>{let t;return(...a)=>{clearTimeout(t);t=setTimeout(()=>f(...a),ms)}};

function renderHeader(user,active){
  if(!$('#header'))return;
  const admin=user.role==='ADMIN';
  const links=admin?[['admin-console.html','Console'],['admin.html','Buses'],['bus-tracking.html','Track buses']]:[['student-dashboard.html','Dashboard'],['routes.html','All routes'],['tracking.html','Track buses']];
  $('#header').innerHTML='<header class="top"><div class="in"><a class="logo" href="index.html">TransitTrack</a><nav aria-label="Main">'+links.map(l=>'<a href="'+l[0]+'" class="'+(l[0]===active?'on':'')+'">'+l[1]+'</a>').join('')+'</nav><span class="who">'+esc(user.name)+' · '+esc(user.role)+'</span><button class="btn sec sm" id="logoutBtn">Log out</button></div></header>';
  $('#logoutBtn').onclick=async()=>{try{await api('/auth/logout',{method:'POST'})}catch(e){}location.href='login.html';};
}

async function requireUser(role,active){
  try{
    const u=await api('/auth/me');
    if(role&&u.role!==role){location.href=u.role==='ADMIN'?'admin-console.html':'student-dashboard.html';return null;}
    renderHeader(u,active);return u;
  }catch(e){return null;}
}