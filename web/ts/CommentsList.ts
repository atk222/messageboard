class CommentsList {
    /**
     * The name of the DOM entry associated with ElementList
     */
    private static readonly NAME = "CommentsList";

    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
     * Initialize the ElementList singleton by creating its element in the DOM.
     * This needs to be called from any public static method, to ensure that the 
     * Singleton is initialized before use.
     */
    /**
     * Initialize the ElementList singleton.  
     * This needs to be called from any public static method, to ensure that the
     * Singleton is initialized before use.
     */
    private static init() {
        if (!CommentsList.isInit) {
            CommentsList.isInit = true;
        }
    }

    /**
     * Removing comments list from view
     */
    public static removeCommentsList() {
        $("#" + CommentsList.NAME + '-commentsSection').remove();
        CommentsList.isInit = false;
    }

    private static goToProfilePage(userProfile) {
        CommentsList.removeCommentsList();
        // OtherProfilePage.refresh(userProfile);
    }
    /**
     * update() is the private method used by refresh() to update the 
     * CommentsList
     */
    private static update(commentsData) {
        console.log("Comments list update");
        // Remove the table of data, if it exists
        CommentsList.removeCommentsList();
        // Use a template to re-generate the table, and then insert it
        //console.log($("#"+ElementList.NAME).length);
        console.log("CommentsList update() commentsData");
        console.log(commentsData);

        // //Make GET request for the comments of the specific post
        // $.ajax({
        //     type: "GET",
        //     url: backendUrl + "/comments/" + postID,
        //     dataType: "json",
        //     success: function (data) {
                
        //     }
        // });

        //data should include user_id so that template checks whether to show edit button or not
        $("#ViewPost").append(Handlebars.templates[CommentsList.NAME + ".hb"](commentsData));

        //Link profile picture to profile page
        //Allow profile image to be clicked
        $("." + CommentsList.NAME + "-image").click(CommentsList.clickProfilePic);

        console.log(commentsData);
        console.log("add comments list");

        // Find all of the Edit buttons, and set their behavior
        $("." + CommentsList.NAME + "-editbtn").click(EditEntryForm.show);
        
        //Need to conditionally remove edit buttons if the userIDs 
        //of each comment do not match with current userID
        $('.' + CommentsList.NAME + '-entry').each(function() {
            console.log('printing comments list entries');
            console.log($(this));
            console.log($(this).data('userid'));
            let currentUserID = localStorage.getItem('userID');
            console.log(currentUserID);
            if ($(this).data('userid') !== currentUserID) {
                // console.log($(this).children('#' + CommentsList.NAME + '-editbtn'));
                $(this).children('.' + CommentsList.NAME + '-editbtn').hide();
            }
        });

    }
    /**
     * refresh() is the public method for updating the ElementList
     */
    public static refresh(postInfo) {
        // Make sure the singleton is initialized
        CommentsList.init();

        //Get postID from the ViewPost html
        console.log("CommentsList refresh() postInfo");
        console.log(postInfo);
        let postID = postInfo.mData.mId;
        
        //Get comments for the specific post
        $.ajax({
            type: "GET",
            url: backendUrl + "/comments/" + postID,
            dataType: "json",
            success: CommentsList.update
        });
    }

    //Go to profile page when clicking on user's profile in the comments
    public static clickProfilePic(event: any) {
        console.log('clickProfile pic from comments list');

        let commentEntry = $(this).parent().parent();
        let userID = commentEntry.data('userid')
        console.log(userID);

        //Pass in userID to load correct profile page
        MainProfilePage.goToProfilePage(userID);
    }

    /**
     * clickEdit is the code we run in response to a click of a edit button
     * @param event Event handler that is automatically passed by jQuery
     */
    private static clickEdit(event: any) {
        // Need to stop inner button from also triggering onclick event on parent element
        event.stopPropagation();

        // as in clickDelete, we need the ID of the row
        let id = $(this).data("value");
        $.ajax({
            type: "GET",
            url: backendUrl + "/messages/" + id,
            dataType: "json",
            success: editEntryForm.init
        });
    }
}