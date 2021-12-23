class LoginPage {
    private static readonly NAME = "LoginPage";

    private static isInit = false;

    private static init() {
        if (!LoginPage.isInit) {
            LoginPage.isInit = true;
        }
        
    }

    public static refresh() {
        console.log("page refresed");
        $("body").append(Handlebars.templates[LoginPage.NAME + ".hb"]());
        // $("." + LoginPage.NAME + "-signInButton").click(LoginPage.signIn);
        $("." + LoginPage.NAME + "-signOutButton").click(LoginPage.signOut);
        // $(".g-signin2").attr("data-onsuccess", LoginPage.signOut);
    }

    /**
     * Removing login page from the view
     */
    public static removeLoginPage() {
        //Remove sign in page
        $("#" + LoginPage.NAME).remove();
        LoginPage.isInit = false;
    }

    private static onSignIn(googleUser) {
        //Need to get user id token to send to backend
        let id_token = googleUser.getAuthResponse().id_token;
        // console.log(typeof id_token);
        //console.log(id_token);

        //POST request to send token to backend
        $.ajax({
            method: "POST",
            url: backendUrl + "/login",
            dataType: "json",
            contentType: "application/x-www-form-urlencoded",
            data: id_token,
            success: function (response) {
                console.log("Token sent successfully");
                //Store sessionID in local storage
                localStorage.setItem('sessionID', response.mData);
                console.log('Session ID:' + response.mData);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                //if sessionID is sent in request and it is 
                //not valid, then user should be logged out here
                console.log("Error logging in");
            }
        });

        //Get current user info and store into userProfile
        let profile = googleUser.getBasicProfile();
        this.changeProfile(googleUser);
        console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
        console.log('Name: ' + profile.getName());
        console.log('Image URL: ' + profile.getImageUrl());
        console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.

        //Store current user ID
        localStorage.setItem('userID', profile.getId());

        //Set state of if signed in to TRUE to change UI to allow user to view messages
        isSignedIn = true;

        //Remove login page
        LoginPage.removeLoginPage();

        //Set UI for viewing messages
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

    /**
     * Changing local storage of logged in user's profile info
     * @param googleUser Object storing the user's profile info
     */
    private static changeProfile(googleUser) {
        //Check if the googleUser exists
        //If not, then user is not logged in
        if (googleUser) {
            // Get `BasicProfile` object
            let profile = googleUser.getBasicProfile();

            /**
             * MAKE AJAX REQUEST TO GET USER COMMENT FOR PROFILE PAGE
             */

            //Need to change info of currently logged in user
            //Ignore this: used for testing
            userProfile = {
                userName: profile.getName(),
                userEmail: profile.getEmail(),
                imageUrl: profile.getImageUrl(),
                userComment: "Test"
            }
            console.log("User profile changed");
        } else {
            // Remove profile information
            // userProfile = null;
            console.log("User profile removed");
        }
    }
}