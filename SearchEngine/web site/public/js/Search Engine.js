var searchArea = document.getElementsByName("searchArea")[0];
var searchButton = document.getElementsByName("searchButton")[0];
var suggestionBox = document.getElementsByClassName("suggestion")[0];
var request = new XMLHttpRequest(); // Ajax object initialization

// Waiting for Ajax response
// implemented once the state of communication changes
request.onreadystatechange = () => {

    if(request.readyState == 4 && request.status == 200)
    {
        // get the sent array in forms of string and split it
        // into array of words
        let response = request.responseText.split(',');
        if(response.length != 0 && response[0] != "")
        {
            for (var i = 0; i < response.length; i++) {
            
                child = document.createElement('h2');
                text = document.createTextNode(response[i]);
                child.appendChild(text);
                suggestionBox.appendChild(child);
                
            }
        }
        
    }
};

// if any new search input
// send request with ajax to get the suggested results
searchArea.onkeyup = function(){

    let data_entered = searchArea.value;

    // delete the previous results
    suggestionBox.innerHTML = '';

    if(data_entered != "")
    {
        request.open('GET', 'http://localhost:8080/ajax?searchArea=' + data_entered, true);
        request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        request.send();
    }
};