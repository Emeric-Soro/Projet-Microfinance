const http = require('http');

function post(path, body) {
  return new Promise((resolve, reject) => {
    const postData = JSON.stringify(body);
    const req = http.request({
      hostname: 'localhost', port: 8080, path, method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Content-Length': Buffer.byteLength(postData) }
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

function uploadMultipart(path, token, filename, fileContent, contentType, categorie) {
  return new Promise((resolve, reject) => {
    const boundary = '----WebKitFormBoundary7MA4YWxkTrZu0gW';
    let body = [];
    
    // Fichier
    body.push(Buffer.from(`--${boundary}\r\n`));
    body.push(Buffer.from(`Content-Disposition: form-data; name="fichier"; filename="${filename}"\r\n`));
    body.push(Buffer.from(`Content-Type: ${contentType}\r\n\r\n`));
    body.push(fileContent);
    body.push(Buffer.from('\r\n'));
    
    // Categorie
    if (categorie) {
      body.push(Buffer.from(`--${boundary}\r\n`));
      body.push(Buffer.from(`Content-Disposition: form-data; name="categorie"\r\n\r\n`));
      body.push(Buffer.from(`${categorie}\r\n`));
    }
    
    body.push(Buffer.from(`--${boundary}--\r\n`));
    
    const totalLength = body.reduce((acc, val) => acc + val.length, 0);
    const req = http.request({
      hostname: 'localhost', port: 8080, path, method: 'POST',
      headers: {
        'Authorization': 'Bearer ' + token,
        'Content-Type': 'multipart/form-data; boundary=' + boundary,
        'Content-Length': totalLength
      }
    }, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try { resolve({ status: res.statusCode, body: JSON.parse(data) }); }
        catch (e) { resolve({ status: res.statusCode, body: data }); }
      });
    });
    
    req.on('error', reject);
    for (const chunk of body) {
      req.write(chunk);
    }
    req.end();
  });
}

async function run() {
  try {
    console.log('--- Test d\'Intégration d\'Upload et Téléchargement de Documents KYC ---\n');

    // Login
    const loginRes = await post('/api/v1/utilisateurs/login', { login: 'demo.admin', motDePasse: 'Demo@12345' });
    const token = loginRes.body.token;
    if (!token) { console.error('Login échoué:', loginRes.body); return; }
    console.log('✅ Login réussi');

    // Trouver un compte et son client propriétaire
    const comptesRes = await get('/api/v1/comptes?page=0&size=50', token);
    const comptes = comptesRes.body.content || [];
    if (!comptes.length) { console.log('Aucun compte trouvé.'); return; }

    const compteLie = comptes.find(c => c.clientNumero);
    if (!compteLie) { 
      console.log('Aucun compte avec clientNumero trouvé.'); 
      return; 
    }
    
    const numCompte = compteLie.numCompteComplet || compteLie.numeroCompte || compteLie.numCompte;
    const clientCode = compteLie.clientNumero;

    // Rechercher le client par son codeClient
    const clientsRes = await get(`/api/v1/clients?page=0&size=100`, token);
    const clientsList = clientsRes.body.content || [];
    const client = clientsList.find(c => c.codeClient === clientCode);
    
    if (!client) {
      console.log(`Impossible de trouver le client ayant le code ${clientCode} en base.`);
      return;
    }
    
    const cId = client.idClient;

    // Charger les détails du client
    const detailsResInit = await get(`/api/v1/clients/${cId}`, token);
    const c = detailsResInit.body;
    console.log(`📋 Client ciblé : ${c.nomComplet} (${c.idClient}) via son compte ${numCompte}`);
    
    // Créer un petit fichier PNG et un petit PDF simulés
    const pngContent = Buffer.from('PNG_SIMULATION_DATA_IMAGE_DUMMY_CONTENT_12345');
    const pdfContent = Buffer.from('%PDF-1.4 DUMMY_PDF_CONTENT_FOR_TESTING');

    console.log('\n1. Upload de la Photo de profil (catégorie PHOTO_PROFIL)...');
    const upPhotoRes = await uploadMultipart(`/api/v1/clients/${c.idClient}/documents`, token, 'test_avatar.png', pngContent, 'image/png', 'PHOTO_PROFIL');
    console.log(`   Statut : ${upPhotoRes.status}`);
    console.log(`   Réponse :`, upPhotoRes.body);
    
    console.log('\n2. Upload de la CNI (catégorie PIECE_IDENTITE)...');
    const upCniRes = await uploadMultipart(`/api/v1/clients/${c.idClient}/documents`, token, 'test_cni.pdf', pdfContent, 'application/pdf', 'PIECE_IDENTITE');
    console.log(`   Statut : ${upCniRes.status}`);
    console.log(`   Réponse :`, upCniRes.body);

    // Re-charger le client pour vérifier les URLs
    const detailsRes = await get(`/api/v1/clients/${c.idClient}`, token);
    const updatedClient = detailsRes.body;
    console.log(`\n3. Vérification des URLs sur l'entité Client :`);
    console.log(`   photoProfilUrl   : ${updatedClient.photoProfilUrl}`);
    console.log(`   photoIdentiteUrl : ${updatedClient.photoIdentiteUrl}`);

    if (updatedClient.photoProfilUrl && updatedClient.photoProfilUrl.startsWith('upload/') &&
        updatedClient.photoIdentiteUrl && updatedClient.photoIdentiteUrl.startsWith('upload/') &&
        updatedClient.photoProfilUrl !== updatedClient.photoIdentiteUrl) {
      console.log('✅ Séparation et mise à jour automatique des URLs d\'upload OK !');
    } else {
      console.log('❌ Échec de la mise à jour ou de la séparation des URLs.');
    }

    // Tester l'obtention des documents pour le compte lié
    console.log(`\n4. Test de listing des documents pour le compte ${numCompte}...`);
    const docsRes = await get(`/api/v1/comptes/${numCompte}/documents`, token);
    console.log(`   Statut listing : ${docsRes.status}`);
    const docsList = docsRes.body.content || docsRes.body || [];
    console.log(`   Nombre de documents trouvés : ${docsList.length}`);
    
    if (docsList.length > 0) {
      const lastDoc = docsList[0];
      console.log(`\n5. Test de téléchargement du document ID ${lastDoc.idDoc} (${lastDoc.nomFichier})...`);
      const dlRes = await get(`/api/v1/comptes/${numCompte}/documents/${lastDoc.idDoc}`, token);
      console.log(`   Statut téléchargement : ${dlRes.status}`);
      console.log(`   Header Content-Type   : ${dlRes.headers ? dlRes.headers['content-type'] : 'indéfini'}`);
      if (dlRes.status === 200) {
        console.log(`✅ Téléchargement réussi et type MIME conservé !`);
      } else {
        console.log(`❌ Échec du téléchargement.`);
      }
    }

  } catch (e) {
    console.error('Erreur dans le test :', e.message);
  }
}

run();
