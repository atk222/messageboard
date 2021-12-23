/**
 * The Navbar Singleton is the navigation bar at the top of the page.  Through
 * its HTML, it is designed so that clicking the "brand" part will refresh the
 * page.  Apart from that, it has an "add" button, which forwards to
 * NewEntryForm
 */
class Navbar {
    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
     * The name of the DOM entry associated with Navbar
     */
    private static readonly NAME = "Navbar";

    /**
     * Initialize the Navbar Singleton by creating its element in the DOM and
     * configuring its button.  This needs to be called from any public static
     * method, to ensure that the Singleton is initialized before use.
     */
    private static init() {
        if (!Navbar.isInit) {
            $("body").append(Handlebars.templates[Navbar.NAME + ".hb"]());
            $("#" + Navbar.NAME + "-addPost").click(NewEntryForm.show);

            $("#" + Navbar.NAME + "-addComment").click(NewCommentForm.show);
            //Hide addComment button when on home page
            $("#" + Navbar.NAME + "-addComment").hide();

            $("#" + Navbar.NAME + "-goToProfile").click(Navbar.goToProfilePageFromNavbar);
            $("#" + Navbar.NAME + "-signOut").click(Navbar.signOut);
            Navbar.isInit = true;
        }
    }

    public static removeNavbar() {
        $("#" + Navbar.NAME).remove();
        Navbar.isInit = false;
    }

    /**
     * Refresh() doesn't really have much meaning for the navbar, but we'd
     * rather not have anyone call init(), so we'll have this as a stub that
     * can be called during front-end initialization to ensure the navbar
     * is configured.
     */
    public static refresh() {
        Navbar.init();
    }

    //Assign to signout button
    public static signOut() {
        let auth2 = gapi.auth2.getAuthInstance();
        auth2.disconnect().then(function () {
            userProfile = null;
            console.log(localStorage.getItem('sessionID'));

            //DELETE request to send token to backend
            $.ajax({
                type: "DELETE",
                url: backendUrl + "/logout/" + localStorage.getItem('sessionID'),
                dataType: "json",
                success: function (response) {
                    console.log("Logout successful");
                    console.log(response);
                    //Delete sessionID in local storage
                    localStorage.removeItem('sessionID');
                    localStorage.removeItem('userID');
                    console.log("Deleted sessionID and userID from localstorage");
                }
            });
            console.log('User signed out.');

            //set state of if user is signed in or not
            isSignedIn = false;

            //Reload page so user is brought back to login page
            //Better to reload page and reset everything or just change UI to go back to login page??
            location.reload();
        });
    }

    private static goToProfilePageFromNavbar() {
        let userID = localStorage.getItem('userID');
        MainProfilePage.goToProfilePage(userID);
    }

    
}