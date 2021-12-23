class EditEntryForm {
    /**
     * The name of the DOM entry associated with NewEntryForm
     */
    private static readonly NAME = "EditEntryForm";

    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
     * Post ID of current comment we are editing
     */
    private static postID;

    /**
     * Comment ID of the comment we are editing
     */
    private static commentID;

    /**
     * Reference to entire comment entry html
     */
    private static commentEntry;

    /**
     * Initialize the NewEntryForm by creating its element in the DOM and 
     * configuring its buttons.  This needs to be called from any public static
     * method, to ensure that the Singleton is initialized before use
     */
    private static init() {
        if (!EditEntryForm.isInit) {
            $("body").append(Handlebars.templates[EditEntryForm.NAME + ".hb"]());
            console.log("loaded editForm");
            $("#" + EditEntryForm.NAME + "-saveChanges").click(EditEntryForm.submitCommentChange);
            $("#" + EditEntryForm.NAME + "-Close").click(EditEntryForm.hide);
            EditEntryForm.isInit = true;
        }
    }

    /**
     * Remove NewEntryForm from view
     */
    public static removeEditEntryForm() {
        $("#" + EditEntryForm.NAME).remove();
        EditEntryForm.isInit = false;
    }

    /**
     * Refresh() doesn't really have much meaning, but just like in sNavbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    public static refresh() {
        EditEntryForm.init();
    }

    /**
     * Hide the NewEntryForm.  Be sure to clear its fields first
     */
    private static hide() {
        $("#" + EditEntryForm.NAME + "-comment").val("");
        $("#" + EditEntryForm.NAME + "-link").val("");
        $("#" + EditEntryForm.NAME).modal("hide");
    }

    /**
     * Show the NewEntryForm.  Be sure to clear its fields, because there are
     * ways of making a Bootstrap modal disapper without clicking Close, and
     * we haven't set up the hooks to clear the fields on the events associated
     * with those ways of making the modal disappear.
     */
    public static show() {
        //Set current comment entry html
        EditEntryForm.commentEntry = $(this).parent();
        console.log("Print commentEntry");
        console.log(EditEntryForm.commentEntry);

        // //Get current comment and set input text
        // let currentComment = EditEntryForm.commentEntry.children('#CommentsList-comment');
        // let comment = EditEntryForm.commentEntry.children();

        // console.log(currentComment);
        // console.log(comment);
        // $("#" + EditEntryForm.NAME + "-comment").val(currentComment);

        //Show modal
        $("#" + EditEntryForm.NAME).modal("show");

        //Need to get post ID and comment ID of comment to associated edit button
        EditEntryForm.commentID = EditEntryForm.commentEntry.data('commentid');
        EditEntryForm.postID = EditEntryForm.commentEntry.data('postid');
        console.log(EditEntryForm.commentID);
        console.log(EditEntryForm.postID);

    }


    /**
     * Send data to submit the form only if the fields are both valid.  
     * Immediately hide the form when we send data, so that the user knows that 
     * their click was received.
     */
    private static submitCommentChange() {
        //Value of current comment
        let currentComment = EditEntryForm.commentEntry.children('#CommentsList-textarea').val();

        //Set text in input field as the currentComment

        let newComment = "" + $("#" + EditEntryForm.NAME + "-comment").val();
        let newLink=""+ $("#" + EditEntryForm.NAME + "-link").val();

        //Check if new comment is empty
        if (newComment === "") {
            window.alert("Error: comment is not valid");
            return;
        }
        
        //Hide edit form
        EditEntryForm.hide();

        //If new comment equal current comment then do not send change
        if (newComment === currentComment) {

            return;
        }

        $.ajax({
            type: "PUT",
            url: backendUrl + "/comments/" + EditEntryForm.postID + '/' + EditEntryForm.commentID,
            dataType: "json",
            data: JSON.stringify({ mComment: newComment,mLink: newLink}),
            success: EditEntryForm.onSubmitResponse
        });
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
            //Request data to update the post info
            $.ajax({
                type: "GET",
                url: backendUrl + "/messages/" + ViewPost.currentPostInfo.mData.mId,
                dataType: "json",
                // TODO: we should really have a function that looks at the return
                //       value and possibly prints an error message.
                success: ViewPost.refresh
            });
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
} // end class EditEntryForm
