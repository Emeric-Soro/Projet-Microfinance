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

async function run() {
  try {
    console.log('--- Test de la Date de Dernière Opération ---\n');

    // Login
    const loginRes = await post('/api/v1/utilisateurs/login', { login: 'demo.admin', motDePasse: 'Demo@12345' });
    const token = loginRes.body.token;
    if (!token) { console.error('Login échoué:', loginRes.body); return; }
    console.log('✅ Login réussi');

    // Lister tous les comptes
    const comptesRes = await get('/api/v1/comptes?page=0&size=20', token);
    const comptes = comptesRes.body.content || [];
    console.log(`\nTrouvé ${comptes.length} comptes :`);
    
    comptes.forEach(c => {
      console.log(`- Compte ${c.numCompteComplet || c.numCompte} | Solde : ${c.solde} FCFA | Derniere Op : ${c.dateDerniereOp}`);
    });

    if (comptes.length > 0) {
      const targetCompte = comptes[0];
      const numCompte = targetCompte.numCompteComplet || targetCompte.numCompte;
      
      console.log(`\nSimuler un versement de 5000 FCFA sur le compte ${numCompte}...`);
      // Versement
      const txRes = await post('/api/v1/caisses/operations/versement', {
        numeroCompte: numCompte,
        montant: 5000,
        motif: 'Versement de test pour DerniereOp'
      }, token);
      
      console.log(`   Statut versement : ${txRes.status}`);
      if (txRes.status !== 201 && txRes.status !== 200) {
        console.log('   Détails erreur:', txRes.body);
        return;
      }
      console.log('✅ Versement effectué avec succès !');

      // Re-charger le compte
      console.log(`\nRe-chargement du compte ${numCompte} pour vérifier la date de dernière opération...`);
      const detailsRes = await get(`/api/v1/comptes/${numCompte}`, token);
      const updatedCompte = detailsRes.body;
      console.log(`   Derniere Op après versement : ${updatedCompte.dateDerniereOp}`);
      
      if (updatedCompte.dateDerniereOp) {
        console.log('✅ La date de dernière opération s\'affiche et s\'est mise à jour correctement !');
      } else {
        console.log('❌ La date de dernière opération est toujours NULL.');
      }
    }
  } catch (e) {
    console.error('Erreur dans le test :', e.message);
  }
}

run();
