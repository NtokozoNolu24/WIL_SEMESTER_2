// Single shared Firebase setup (same project as the mobile app)
const firebaseConfig = {
  apiKey: "AIzaSyDjEcGxpLk_DMeiYRvjlmAHCO685K-Pejs",
  authDomain: "mzansiautorepairs-41f49.firebaseapp.com",
  projectId: "mzansiautorepairs-41f49",
  storageBucket: "mzansiautorepairs-41f49.firebasestorage.app",
  messagingSenderId: "1020590438428",
  appId: "1:1020590438428:web:0bdf0494b75f3c8ced87d9"
};
if (!firebase.apps.length) firebase.initializeApp(firebaseConfig);
const auth = firebase.auth();
const db = firebase.firestore();
