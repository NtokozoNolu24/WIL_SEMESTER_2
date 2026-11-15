// Shared navigation bar – edit it here and every page updates.
(function () {
  const page = (location.pathname.split('/').pop() || 'index.html').toLowerCase();
  const links = [['index.html','Home'],['services.html','Services'],['about.html','About'],
                 ['reviews.html','Reviews & Ratings'],['contact.html','Contact']];
  const active = h => h === page ? ' class="active"' : '';
  const html = `
<nav class="mz-nav"><div class="mz-inner">
  <a class="mz-brand" href="index.html"><i class="fa-solid fa-screwdriver-wrench"></i><span>Mzansi Auto Repairs</span></a>
  <button class="mz-burger" id="menuButton" type="button" aria-label="Open navigation"><i class="fa-solid fa-bars"></i></button>
  <div class="mz-links" id="navLinks">
    ${links.map(l => `<a href="${l[0]}"${active(l[0])}>${l[1].replace('&','&amp;')}</a>`).join('')}
    <a href="dashboard.html" id="dashboardNav"${page==='dashboard.html'?' class="active"':''} style="display:none">Dashboard</a>
    <a href="login.html" id="loginNav" class="mz-login">Login</a>
    <a href="#" id="logoutNav" class="mz-logout" style="display:none">Logout</a>
  </div></div></nav>`;
  const css = `
body{padding-top:72px!important}
.mz-nav{position:fixed;top:0;left:0;right:0;z-index:1000;background:linear-gradient(135deg,#0b1d2e,#1a3a5c);box-shadow:0 4px 20px rgba(0,0,0,.2);font-family:'Segoe UI',Tahoma,sans-serif}
.mz-inner{max-width:1200px;margin:auto;padding:0 22px;min-height:72px;display:flex;align-items:center;justify-content:space-between;gap:20px;flex-wrap:wrap}
.mz-brand{display:flex;align-items:center;gap:10px;color:#fff;font-size:1.15rem;font-weight:700;text-decoration:none}
.mz-brand i{color:#d4a843;font-size:1.5rem}
.mz-links{display:flex;align-items:center;gap:5px}
.mz-links a{color:#fff;padding:9px 12px;border-radius:7px;font-size:.9rem;text-decoration:none;transition:.2s}
.mz-links a:hover,.mz-links a.active{background:#d4a843;color:#0b1d2e}
.mz-links a.mz-login{background:#d4a843;color:#0b1d2e;font-weight:700;border-radius:25px;padding:9px 18px}
.mz-links a.mz-logout{background:#c0392b;color:#fff;border-radius:25px;padding:9px 18px}
.mz-burger{display:none;background:none;border:0;color:#fff;font-size:1.6rem;cursor:pointer;padding:8px}
@media(max-width:900px){.mz-burger{display:block}.mz-links{display:none;width:100%;flex-direction:column;align-items:stretch;padding:15px 0 20px}.mz-links.open{display:flex}}`;
  const me = document.currentScript;
  const st = document.createElement('style'); st.textContent = css; document.head.appendChild(st);
  if (!document.querySelector('link[href*="font-awesome"]')) {
    const l = document.createElement('link'); l.rel = 'stylesheet';
    l.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css'; document.head.appendChild(l);
  }
  me.insertAdjacentHTML('beforebegin', html);

  // We own the menu and logout buttons: stop older page-level handlers firing twice.
  document.addEventListener('click', async e => {
    const burger = e.target.closest('#menuButton'), out = e.target.closest('#logoutNav');
    if (burger) { e.stopImmediatePropagation(); document.getElementById('navLinks').classList.toggle('open'); }
    if (out) {
      e.preventDefault(); e.stopImmediatePropagation();
      if (confirm('Are you sure you want to log out?')) { try { await firebase.auth().signOut(); } catch (x) {} location.href = 'index.html'; }
    }
  }, true);

  const cfg = {apiKey:"AIzaSyCxmekWI5XBPMwCfSdTZso6hTZSgZpZQLo",authDomain:"mzansiautorepairs-780ef.firebaseapp.com",
    projectId:"mzansiautorepairs-780ef",storageBucket:"mzansiautorepairs-780ef.firebasestorage.app",
    messagingSenderId:"157253497706",appId:"1:157253497706:web:81f60557a9d96365922b13"};
  const load = src => new Promise(r => { const s = document.createElement('script'); s.src = src; s.onload = r; s.onerror = r; document.head.appendChild(s); });
  async function start() {
    const base = 'https://www.gstatic.com/firebasejs/9.22.0/';
    if (typeof firebase === 'undefined') await load(base + 'firebase-app-compat.js');
    if (typeof firebase === 'undefined') return;
    if (!firebase.auth) await load(base + 'firebase-auth-compat.js');
    if (!firebase.apps.length) firebase.initializeApp(cfg);
    firebase.auth().onAuthStateChanged(u => {
      document.getElementById('loginNav').style.display = u ? 'none' : 'inline-block';
      document.getElementById('logoutNav').style.display = u ? 'inline-block' : 'none';
      document.getElementById('dashboardNav').style.display = u ? 'inline-block' : 'none';
    });
  }
  document.addEventListener('DOMContentLoaded', start);
})();
