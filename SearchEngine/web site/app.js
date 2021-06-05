const express = require('express');
const fs = require('fs');
const natural = require('natural');     // import the natural language processing module
const app = express();
let data = fs.readFileSync('DB.json');  // read the database json file
let simple_data = JSON.parse(data);     // conversion of the json file into js object
let send_data = new Array();            // Array for search data to be send
let suggestion_data = new Array();      // Array for the suggested queries
let send_suggestion_data = new Array(); // Buffer for send the relevant words from suggested data

// this function is for filtering the array to only unique values
function onlyUnique(value, index, self) {
    return self.indexOf(value) === index;
  }

// Listening for Port 8080 for the requests
app.listen(8080, (err)=>{
    if(err)
    console.log(err);
    else
    console.log("Server starts listening at port 8080");
});

app.use(express.static('public'));
app.set('view engine', 'ejs');

// Render the home page
app.get('/', (req, res)=>{
    res.render('Search Engine');
});

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "YOUR-DOMAIN.TLD"); // update to match the domain you will make the request from
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
  });

// Render the results of the search page
app.get('/result', (req, res)=>{

    // read the data value entered from the search field
    // using the natural language processing stemmer in order to get the root of the word
    let searchWord = natural.PorterStemmer.stem(req.query.searchArea);
    let reg = new RegExp(searchWord, 'i');
    
    if(searchWord != "")    // if the word is null word so, don't search
    {
        for (var i in simple_data) {
            if (i.search(reg) === 0) {
                let o = {};
                o.word = i;
                o.url = simple_data[i];
                send_data.push(o);
                suggestion_data.push(o.word);
            }
        }
        res.render('result', {
            arr: send_data
        });
    }
    else
    {
        res.send("Error you have to enter some word");
    }
    
    // clearing the data in the array
    send_data = [];

    // filter the array for unique data only
    // Add them to the suggestion array
    suggestion_data = suggestion_data.filter(onlyUnique);
});

// handle the ajax request by sending the relevant suggested results to the search box
app.get('/ajax', (req, res) => {

    // read the data value entered from the search field
    let searchWord = req.query.searchArea;
    let reg = new RegExp(searchWord, 'i');

    if(searchWord != "")    // if the word is empty word so, don't search
    {
        for (var i = 0; i < suggestion_data.length; i++) {
            if (suggestion_data[i].search(reg) == 0) {
                send_suggestion_data.push(suggestion_data[i]);
            }
        }
        res.send(send_suggestion_data.join());
    }

    // clear the data from the buffer after send
    send_suggestion_data = [];

});

