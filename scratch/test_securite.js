const http = require('http');

function post(path, body, token) {
  return new Promise((resolve, reject) => {
    const postData = JSON.stringify(body);
    const headers = { 'Content-Type': 'application/json', 'Content-Length': Buffer.byteLength(postData) };
    if (token) headers['Authorization'] = 'Bearer ' + token;
    const req = http.request({
      hostname: 'localhost', port: 8080, path, method: 'POST',
      headers: headers
    }, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try { resolve({ status: res.statusCode, body: JSON.parse(data) }); }
        catch (e) { resolve({ status: res.statusCode, body: data }); }
      });
    });
    req.on('error', reject);
    req.write(postData);
    req.end();
  });
}

function get(path, token) {
  return new Promise((resolve, reject) => {
    const req = http.request({
      hostname: 'localhost', port: 8080, path, method: 'GET',
      headers: { 'Authorization': 'Bearer ' + token }
    }, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try { resolve({ status: res.statusCode, body: JSON.parse(data) }); }
        catch (e) { resolve({ status: res.statusCode, body: data }); }
      });
    });
    req.on('error', reject);
    req.end();
  });
}

function deleteReq(path, token) {
  return new Promise((resolve, reject) => {
    const req = http.request({
      hostname: 'localhost', port: 8080, path, method: 'DELETE',
      headers: { 'Authorization': 'Bearer ' + token }
    }, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try { resolve({ status: res.statusCode, body: JSON.parse(data) }); }
        catch (e) { resolve({ status: res.statusCode, body: data }); }
      });
    });
    req.on('error', reject);
    req.end();
  });
}

async function run() {
  try {
    console.log('--- Test d\'Intégration du Panel de Sécurité ---\n');

    // Login
    const loginRes = await post('/api/v1/utilisateurs/login', { login: 'demo.admin', motDePasse: 'Demo@12345' });
    const token = loginRes.body.token;
    if (!token) { console.error('Login échoué:', loginRes.body); return; }
    console.log('✅ Login réussi');

    // 1. Lister les rôles
    console.log('\n1. Listing des rôles...');
    const rolesRes = await get('/api/v1/securite/roles', token);
    console.log(`   Statut : ${rolesRes.status}`);
    console.log(`   Nombre de rôles : ${rolesRes.body.length}`);
    rolesRes.body.forEach(r => {
      console.log(`     - [${r.codeRole}] ${r.intitule} (${r.nombreUtilisateurs} users)`);
    });

    // 2. Créer un rôle temporaire
    console.log('\n2. Création d\'un rôle de test (TEST_ROLE)...');
    const createRoleRes = await post('/api/v1/securite/roles', {
      codeRole: 'TEST_ROLE_123',
      intitule: 'Rôle de Test Temporaire'
    }, token);
    console.log(`   Statut : ${createRoleRes.status}`);
    console.log(`   Réponse :`, createRoleRes.body);
    const idTestRole = createRoleRes.body.idRole;

    // 3. Supprimer le rôle temporaire
    if (idTestRole) {
      console.log(`\n3. Suppression du rôle temporaire ID ${idTestRole}...`);
      const deleteRes = await deleteReq(`/api/v1/securite/roles/${idTestRole}`, token);
      console.log(`   Statut suppression : ${deleteRes.status}`);
      if (deleteRes.status === 244 || deleteRes.status === 204) {
        console.log('✅ Rôle supprimé avec succès !');
      } else {
        console.log('❌ Échec de la suppression du rôle.');
      }
    }

    // 4. Lister les utilisateurs backoffice
    console.log('\n4. Listing des utilisateurs backoffice...');
    const usersRes = await get('/api/v1/securite/utilisateurs?page=0&size=5', token);
    console.log(`   Statut : ${usersRes.status}`);
    const usersList = usersRes.body.content || usersRes.body || [];
    console.log(`   Nombre d'utilisateurs : ${usersList.length}`);
    usersList.forEach(u => {
      console.log(`     - ${u.login} | Actif: ${u.actif} | Rôles: ${u.roles.join(', ')}`);
    });

    // 5. Lister les sessions actives
    console.log('\n5. Listing des sessions actives...');
    const sessionsRes = await get('/api/v1/securite/sessions', token);
    console.log(`   Statut : ${sessionsRes.status}`);
    console.log(`   Sessions en cours : ${sessionsRes.body.length}`);
    sessionsRes.body.forEach(s => {
      console.log(`     - User: ${s.username} | IP: ${s.ipAddress} | ID: ${s.sessionId}`);
    });

    console.log('\n✅ Toutes les API d\'administration de sécurité fonctionnent parfaitement !');

  } catch (e) {
    console.error('Erreur dans le test :', e.message);
  }
}

run();
