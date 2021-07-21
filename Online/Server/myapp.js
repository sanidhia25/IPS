const express = require('express')
const app = express()
const mongoClient = require('mongodb').MongoClient

const url = "mongodb://localhost:27017"

app.use(express.json())

mongoClient.connect(url, (err, db) => {

    if (err) {
        console.log("Error while connecting mongo client")
    } else {

        const IPS = db.db('IPS')
        const Online = IPS.collection('Online')
        const Offline = IPS.collection('Offline')

        app.post('/index', (req, res) => {

            const newLocation = {
                Xcood: req.body.Xcood,
                Ycood: req.body.Ycood,
                Timestamp: req.body.TS,
                Identity: req.body.Identity
            }
            Online.insertOne(newLocation,(err,result) =>{
                res.status(200).send()
            })

        })

        app.post('/train', (req, res) => {
            console.log(req.body)
            
            const newScan = {
                BSSID: req.body.BSSID,
                SSID: req.body.SSID,
                X : req.body.X,
                Y : req.body.Y,
                RSS : req.body.RSS,
                timestamp : req.body.timestamp
            }

            Offline.insertOne(newScan, (err,result)=>{
                res.status(200).send()
            })
        })

    }

})

app.listen(3000, () => {
    console.log("Listening on port 3000...")
})