/// <reference path="ts/EditEntryForm.ts"/>
/// <reference path="ts/Navbar.ts"/>
/// <reference path="ts/NewEntryForm.ts"/>
/// <reference path="ts/ElementList.ts"/>
/// <reference path="ts/LoginPage.ts"/>
/// <reference path="ts/MainProfilePage.ts"/>
/// <reference path="ts/ViewPost.ts"/>
/// <reference path="ts/CommentsList.ts"/>
/// <reference path="ts/NewCommentForm.ts"/>

// window.event.cancelBubble = true;

// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
//var $: any;
//var sub: any;
// Prevent compiler errors when using Handlebars
let Handlebars: any;
/// This constant indicates the path to our backend server
const backendUrl = "https://cse216project.herokuapp.com";

//Need initialize use of Google OAuth
//Entire app needs to have access to it to make requests to backend
//Hosted domain restricts which domains are allowed to login
gapi.load('auth2', function () {
    gapi.auth2.init({
        client_id: "649625495044-a7b17r9ktheh82dgbflb5f18a9k34dea.apps.googleusercontent.com",
        scope: "email",
        hosted_domain: "lehigh.edu",
        fetch_basic_profile: "true",
    });
});

//State of app if signed in or not
//Store in localStorage
let isSignedIn = false;

// Global for user profile info after OAuth sign in
// userProfile is object with following name:values
// userName: "name",
// userEmail: "email",
// imageUrl: "imageLink",
// userComment: "comment"
// Look at LoginPage.ts for more info
let userProfile;

let sampleComment = {
    imageUrl: "https://lh3.googleusercontent.com/-2iTivd2-qZw/AAAAAAAAAAI/AAAAAAAAAAA/AKF05nAs_uZ1VBno9uZlVW3Ul0h6fv0HAw/s96-c/photo.jpg",
    userName: "Andrew ha",
    comment: "Test comment"
}

// a global for the EditEntryForm of the program.  See newEntryForm for
// explanation
let editEntryForm: EditEntryForm;

// Run some configuration code when the web page loads
$(document).ready(function () {
    if (!isSignedIn) {
        LoginPage.refresh();
    } else {
        Navbar.refresh();
        NewEntryForm.refresh(); //uncommented this line 2pm 2/24 seemed to fix not adding
        NewCommentForm.refresh();
        ElementList.refresh();
        EditEntryForm.refresh();

        // // Create the object that controls the "Edit Entry" form
        // editEntryForm = new EditEntryForm();
        // // set up initial UI state
        // $("#editElement").show();
    }
});
