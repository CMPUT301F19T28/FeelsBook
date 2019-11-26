const functions = require('firebase-functions');
const admin = require('firebase-admin');
const algoliasearch = require('algoliasearch');

const ALOGLIA_APP_ID = "CZHQW4KJVA";
const ALGOLIA_ADMIN_KEY = "394205d7e7f173719c08f3e187b2a77b";
const ALGOLIA_INDEX_NAME = 'users';

admin.initializeApp(functions.config().firebase);

exports.addFirestoreDataToAlgolia = functions.https.onRequest((req, res) => {

    var arr = [];

    admin.firestore().collection("users").get().then((docs) => {
        docs.forEach((doc) => {
            let user = doc.data();
            user.objectID =  doc.id;

            arr.push(user);
        })

        var client = algoliasearch(ALOGLIA_APP_ID,ALGOLIA_ADMIN_KEY);
        var index = client.initIndex(ALGOLIA_INDEX_NAME);

        index.saveObjects(arr, function (err, content) {
            res.status(200).send(content);
        })
    })
})