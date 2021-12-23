/**
 * NewCommentForm encapsulates all of the code for the form for adding an entry
 */
class NewCommentForm {

    /**
     * The name of the DOM entry associated with NewCommentForm
     */
    private static readonly NAME = "NewCommentForm";

    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
     * Initialize the NewCommentForm by creating its element in the DOM and 
     * configuring its buttons.  This needs to be called from any public static
     * method, to ensure that the Singleton is initialized before use
     */
    private static init() {
        if (!NewCommentForm.isInit) {
            $("body").append(Handlebars.templates[NewCommentForm.NAME + ".hb"]());
            $("#" + NewCommentForm.NAME + "-Submit").click(NewCommentForm.submitForm);
            $("#" + NewCommentForm.NAME + "-Close").click(NewCommentForm.hide);
            NewCommentForm.isInit = true;
        }
    }

    /**
     * Remove NewCommentForm from view
     */
    public static removeNewCommentForm() {
        $("#" + NewCommentForm.NAME).remove();
        NewCommentForm.isInit = false;
    }

    /**
     * Refresh() doesn't really have much meaning, but just like in sNavbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    public static refresh() {
        NewCommentForm.init();
    }

    /**
     * Hide the NewCommentForm.  Be sure to clear its fields first
     */
    private static hide() {
        $("#" + NewCommentForm.NAME + "-title").val("");
        $("#" + NewCommentForm.NAME + "-message").val("");
        $("#" + NewCommentForm.NAME + "-link").val("");
        $("#" + NewCommentForm.NAME).modal("hide");
    }

    /**
     * Show the NewCommentForm.  Be sure to clear its fields, because there are
     * ways of making a Bootstrap modal disapper without clicking Close, and
     * we haven't set up the hooks to clear the fields on the events associated
     * with those ways of making the modal disappear.
     */
    public static show() {
        console.log("New comment form show");
        $("#" + NewCommentForm.NAME + "-comment").val("");
        $("#" + NewCommentForm.NAME + "-link").val("");
        $("#" + NewCommentForm.NAME).modal("show");
    }


    /**
     * Send data to submit the form only if the fields are both valid.  
     * Immediately hide the form when we send data, so that the user knows that 
     * their click was received.
     */
    private static submitForm() {
        //Get current value of text in the comment form and the link
        let comment = "" + $("#" + NewCommentForm.NAME + "-comment").val();
        let link ="" + $("#" + NewCommentForm.NAME + "-link").val();
        //get the file from the upload file tab
        const selectedFile = document.getElementById("NewCommentForm-file").files[0];
        if (comment === "") {
            window.alert("Error: comment is not valid");
            return;
        }
        //if you posted a file 
        if(selectedFile!=null){
            //After clicking submit, hide the form for adding a comment
            NewCommentForm.hide();
       
            let postID = $('#ViewPost').data('postid');
            let userID = localStorage.getItem('userID');

            console.log('new comment form postID and userID');
            console.log(postID);
            console.log(userID);
            console.log("New comment" + comment);
            //fileName and fileType
            let TheFileName="";
            let fileType="";
            fileType=selectedFile.type;
            TheFileName=selectedFile.name;
            //console.log(TheFileName);
            //console.log(fileType);
            let biteString;
            //changing the file link
            var read = new FileReader();
            read.readAsDataURL(selectedFile);
            read.onloadend = function(){
                biteString=read.result;
                let userID = localStorage.getItem('userID');
                $.ajax({
                    type: "POST",
                    url: backendUrl + "/comments/" + postID + '/' + userID,
                    dataType: "json",
                    data: JSON.stringify({ mUserID: userID, mPostID: postID, mComment: comment,mLink: link,file: biteString,fileContent: fileType,fileName: TheFileName}),
                    success: NewCommentForm.onSubmitResponse
                });
            }
        }
        //when you do not have a file
        else{
            //After clicking submit, hide the form for adding a comment
            NewCommentForm.hide();
       
            let postID = $('#ViewPost').data('postid');
            let userID = localStorage.getItem('userID');

            console.log('new comment form postID and userID');
            console.log(postID);
            console.log(userID);
            console.log("New comment" + comment);
        
            // set up an AJAX post.  When the server replies, the result will go to
            // onSubmitResponse
            $.ajax({
                type: "POST",
                url: backendUrl + "/comments/" + postID + '/' + userID,
                dataType: "json",
                data: JSON.stringify({ mUserID: userID, mPostID: postID, mComment: comment,mLink: link }),
                success: NewCommentForm.onSubmitResponse
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
            let postID = $('#ViewPost').data('postid');
            //Give the CommentsList refresh() function the postID to
            //allow it to reload the comments for this post
            let mData = {
                mData: {
                    mId: postID
                }
            }
            CommentsList.refresh(mData);
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
