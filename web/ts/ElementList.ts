/**
 * The ElementList Singleton provides a way of displaying all of the data
 * stored on the server as an HTML table.
 */

class ElementList {
    /**
     * The name of the DOM entry associated with ElementList
     */
    private static readonly NAME = "ElementList";

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
        if (!ElementList.isInit) {
            ElementList.isInit = true;
    	}
    }

    /**
     * Removing element list from view
     */
    public static removeElementList() {
        $("#" + ElementList.NAME).remove();
        ElementList.isInit = false;
    }

    private static goToViewPostPage(postInfo) {
        console.log("postInfo before going to ViewPost page");
        console.log(postInfo);
        ElementList.removeElementList();
        ViewPost.refresh(postInfo);
    }
    /**
     * update() is the private method used by refresh() to update the 
     * ElementList
     */
    private static update(data: any) {
        // Remove the table of data, if it exists
        ElementList.removeElementList();
        console.log("Printing posts list");
        console.log(data);
        // Use a template to re-generate the table, and then insert it
        //console.log($("#"+ElementList.NAME).length);
        $("body").append(Handlebars.templates[ElementList.NAME + ".hb"](data));
        

        // Find all of the delete buttons, and set their behavior
        $("." + ElementList.NAME + "-delbtn").click(ElementList.clickDelete);

        // Find all of the Edit buttons, and set their behavior
        $("." + ElementList.NAME + "-editbtn").click(ElementList.clickEdit);

	    //Find all of the Like buttons and set their behavior
        $("." + ElementList.NAME + "-likebtn").click(ElementList.clickLike);

        //Find all dislike buttons and set their behavior
        $("." + ElementList.NAME + "-dlikebtn").click(ElementList.clickDislike);

        //Allow each comment to be clickable and directs to another page to edit message and view comments
        $("." + ElementList.NAME + "-commentBtn").click(ElementList.clickComment);

        //Allow profile image to be clicked
        $("." + ElementList.NAME + "-image").click(ElementList.clickProfilePic);

        //Need to conditionally remove edit buttons if the userIDs 
        //of each post do not match with current userID
        $('#' + ElementList.NAME + '-post').each(function(){
            let currentUserID = localStorage.getItem('userID');
            if(currentUserID !== $(this).data('userid')){
                $(this).children('#' + ElementList.NAME + '-editbtn').hide();
            }
         });


    }
    /**
     * refresh() is the public method for updating the ElementList
     */
    public static refresh() {
        // Make sure the singleton is initialized
        ElementList.init();
        // Issue a GET, and then pass the result to update()
        let currentSessionID = localStorage.getItem('sessionID');
        $.ajax({
            type: "GET",
            url: backendUrl + "/messages",
            dataType: "json", 
            success: ElementList.update,
            error: function (error) {
                //If incorrect session ID is given, then app will automatically logout
                if (error.responseText === "Invalid session ID") {
                    Navbar.signOut();
                }
                // console.log("erororeoo");
                // console.log(error);
            }
        });
    }
    /**
     * clickDelete is the code we run in response to a click of a delete button
     * @param event Event handler that is automatically passed by jQuery
     */
    private static clickDelete(event: any) {
        // Need to stop inner button from also triggering onclick event on parent element
        event.stopPropagation();
        
        // for now, just print the ID that goes along with the data in the row
        // whose "delete" button was clicked
        let id = $(this).data("value");
        $.ajax({
            type: "DELETE",
            url: backendUrl + "/messages/" + id,
            dataType: "json",
            // TODO: we should really have a function that looks at the return
            //       value and possibly prints an error message.
            success: ElementList.refresh
        });
    }

    /**
     * clickEdit is the code we run in response to a click of a delete button
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
    /**
     * clickLike is the code we run in response to a click of a like button
     * @param event Event handler that is automatically passed by jQuery
     */
    private static clickLike(event: any) {
        // Need to stop inner button from also triggering onclick event on parent element
        event.stopPropagation();
        
        // as in clickDelete, we need the ID of the row
        let postID = $(this).data("value");
        let currentUserID = localStorage.getItem('userID');

        $.ajax({
            type: "PUT",
            url: backendUrl + "/messages/" + postID + "/" + currentUserID + "/likes",
	        //url modified to support route to backend
            dataType: "json",
            success: ElementList.refresh    //refreshing the page after a like
        });
    }
    /**
     * clickDislike is the code that should be executed upon a dislike click
     * @param event Event handler that is automatically passed by jQuery
     */
    private static clickDislike(event: any) {
        // Need to stop inner button from also triggering onclick event on parent element
        event.stopPropagation();

        // as in clickDelete, we need the ID of the row
        let postID = $(this).data("value");
        let currentUserID = localStorage.getItem('userID');

        $.ajax({
            type: "PUT",
            url: backendUrl + "/messages/" + postID + "/" + currentUserID + "/dislikes",
            dataType: "json",
            contentType: 'application/json',
            success: ElementList.refresh
        });
    }
    
    /**
     * Clicking comment to direct to another page that
     * allows you to edit and view comments
     */
    private static clickComment(event: any) {
        //Get id of row and request info on that message
        let id = $(this).data("value");
        $.ajax({
            type: "GET",
            url: backendUrl + "/messages/" + id,
            dataType: "json",
            // TODO: we should really have a function that looks at the return
            //       value and possibly prints an error message.
            success: ElementList.goToViewPostPage
        });
    }

    public static clickProfilePic(event: any) {
        event.stopPropagation();
        console.log('clickProfile pic');

        let postEntry = $(this).parent().parent();
        let userID = postEntry.data('userid')
        console.log(userID);

        MainProfilePage.goToProfilePage(userID);
    }
}
