// Single shared Firebase setup (same project as the mobile app)
const firebaseConfig = {
  apiKey: "AIzaSyCxmekWI5XBPMwCfSdTZso6hTZSgZpZQLo",
  authDomain: "mzansiautorepairs-780ef.firebaseapp.com",
  projectId: "mzansiautorepairs-780ef",
  storageBucket: "mzansiautorepairs-780ef.firebasestorage.app",
  messagingSenderId: "157253497706",
  appId: "1:157253497706:web:81f60557a9d96365922b13"
};
if (!firebase.apps.length) firebase.initializeApp(firebaseConfig);
const auth = firebase.auth();
const db = firebase.firestore();
