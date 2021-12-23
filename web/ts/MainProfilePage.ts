class MainProfilePage {
    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;
    
    /**
     * The name of the DOM entry associated with Navbar
     */
    private static readonly NAME = "MainProfilePage";

    /**
     * Store userID of user we are currently viewing
     */
    private static userID;
    
    /**
     * Check if singleton is initialized yet
     */
    private static init() {
        if(!MainProfilePage.isInit) {
            MainProfilePage.isInit = true;
        }
    }

    public static removeMainProfilePage() {
        $("#" + MainProfilePage.NAME).remove();
        MainProfilePage.isInit = false;
    }

    //Need to give userID
    public static goToProfilePage(userID: String) {
        //Remove current elements on page
        ElementList.removeElementList();
        NewEntryForm.removeNewEntryForm();
        ViewPost.removeViewPostPage();
        CommentsList.removeCommentsList();
        MainProfilePage.removeMainProfilePage();

        //Hide Add Post button
        $("#" + 'Navbar-addPost').hide();
        $("#" + 'Navbar-addComment').hide();

        MainProfilePage.userID = userID;
        MainProfilePage.refresh(userID);
    }

    /**
     * Load UI with userProfile info
     */
    public static refresh(userID: String) {
        console.log("user id refresh()");
        console.log(userID);
        $.ajax({
            type: "GET",
            url: backendUrl + "/user/" + userID,
            dataType: "json",
            success: MainProfilePage.update
        });
        

    }

    public static update(userProfile: any) {
        console.log('update Profile page');
        console.log(userProfile);
        $("body").append(Handlebars.templates[MainProfilePage.NAME + ".hb"](userProfile.mData));
        $("#" + MainProfilePage.NAME + "-submitBtn").click(MainProfilePage.submitCommentChange);

        //Hide change comment button if current user ID does not match user ID of the profile
        console.log(userProfile.mData);
        console.log(localStorage.getItem('userID'));

        if (userProfile.mData.uUserID !== localStorage.getItem('userID')) {
            $("#" + MainProfilePage.NAME + "-submitBtn").hide();
        }
    }

    //WORK IN PROGRESS
    private static submitCommentChange() {
        console.log("comment change");
        let newComment = $("#" + MainProfilePage.NAME + "-userComment").val();
        console.log(newComment);
        // console.log(userProfile.userComment);

        //If user does not change comment but still clicks submit, the request will not be sent
        if (!(newComment === userProfile.userComment)) {
            //MAKE POST REQUEST TO UPDATE NEW COMMENT
            console.log("changed to different comment");
            $.ajax({
                type: "PUT",
                url: backendUrl + "/user/" + MainProfilePage.userID,
                dataType: "json",
                contentType: "application/x-www-form-urlencoded",
                data: newComment,
                success: MainProfilePage.onSubmitResponse
            });
        }
    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * 
     * @param data The object returned by the server
     */
    private static onSubmitResponse(data: any) {
        // If we get an "ok" message, clear the form and refresh the main 
        // listing of messages
        if (data.mStatus === "ok") {
            //Reload the profile page
            MainProfilePage.goToProfilePage(MainProfilePage.userID);
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }
}