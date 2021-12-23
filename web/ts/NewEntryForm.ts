/**
 * NewEntryForm encapsulates all of the code for the form for adding an entry
 */
class NewEntryForm {

    /**
     * The name of the DOM entry associated with NewEntryForm
     */
    private static readonly NAME = "NewEntryForm";

    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
     * Initialize the NewEntryForm by creating its element in the DOM and 
     * configuring its buttons.  This needs to be called from any public static
     * method, to ensure that the Singleton is initialized before use
     */
    private static init() {
        if (!NewEntryForm.isInit) {
            $("body").append(Handlebars.templates[NewEntryForm.NAME + ".hb"]());
            $("#" + NewEntryForm.NAME + "-OK").click(NewEntryForm.submitForm);
            $("#" + NewEntryForm.NAME + "-Close").click(NewEntryForm.hide);
            NewEntryForm.isInit = true;
        }
    }

    /**
     * Remove NewEntryForm from view
     */
    public static removeNewEntryForm() {
        $("#" + NewEntryForm.NAME).remove();
        NewEntryForm.isInit = false;
    }

    /**
     * Refresh() doesn't really have much meaning, but just like in sNavbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    public static refresh() {
        NewEntryForm.init();
    }

    /**
     * Hide the NewEntryForm.  Be sure to clear its fields first
     */
    private static hide() {
        $("#" + NewEntryForm.NAME + "-title").val("");
        $("#" + NewEntryForm.NAME + "-message").val("");
        $("#" + NewEntryForm.NAME + "-link").val("");
        $("#" + NewEntryForm.NAME + "-file").val("");
        $("#" + NewEntryForm.NAME).modal("hide");
    }

    /**
     * Show the NewEntryForm.  Be sure to clear its fields, because there are
     * ways of making a Bootstrap modal disapper without clicking Close, and
     * we haven't set up the hooks to clear the fields on the events associated
     * with those ways of making the modal disappear.
     */
    public static show() {
        $("#" + NewEntryForm.NAME + "-title").val("");
        $("#" + NewEntryForm.NAME + "-message").val("");
        $("#" + NewEntryForm.NAME + "-link").val("");
        $("#" + NewEntryForm.NAME + "-file").val("");
        $("#" + NewEntryForm.NAME).modal("show");
    }


    /**
     * Send data to submit the form only if the fields are both valid.  
     * Immediately hide the form when we send data, so that the user knows that 
     * their click was received.
     */
    private static submitForm() {
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let title = "" + $("#" + NewEntryForm.NAME + "-title").val();
        let msg = "" + $("#" + NewEntryForm.NAME + "-message").val();
        let link= "" +$("#" + NewEntryForm.NAME + "-link").val();
        let location = "" +$("#" + NewEntryForm.NAME + "-location").val();
        
        //get the file from the upload file tab
        const selectedFile = document.getElementById("NewEntryForm-file").files[0];
        
        if (title === "" || msg === "" || location === "nothing") {
            window.alert("Error: title or message  or location is not valid");
            return;
        }
        console.log(title);
        console.log(location);
       // console.log(title);

        //if you posted a file
        if(selectedFile!=null){
            //fileName and fileType
            let TheFileName="";
            let fileType="";
            fileType=selectedFile.type;
            TheFileName=selectedFile.name;
            //console.log(TheFileName);
            //console.log(fileType);
            let biteString;
            var read = new FileReader();
            //converting file into a biteString
            read.readAsDataURL(selectedFile);
            read.onloadend = function(){
                biteString=read.result;
                console.log(biteString);
                NewEntryForm.hide();
                let userID = localStorage.getItem('userID');
                // set up an AJAX post.  When the server replies, the result will go to
                // onSubmitResponse
                $.ajax({
                    type: "POST",
                    url: backendUrl + "/messages",
                    dataType: "json",
                    data: JSON.stringify({ mUserID: userID, mTitle: title, mMessage: msg, mLink: link,file: biteString,fileContent: fileType,fileName: TheFileName, mLocation: location}),
                    success: NewEntryForm.onSubmitResponse
                });
            }
        }

        //when you do not have a file posted
        else{
            NewEntryForm.hide();
            let userID = localStorage.getItem('userID');
            $.ajax({
                type: "POST",
                url: backendUrl + "/messages",
                dataType: "json",
                data: JSON.stringify({ mUserID: userID, mTitle: title, mMessage: msg, mLink: link, mLocation: location}),
                success: NewEntryForm.onSubmitResponse
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
        // listing of posts
        if (data.mStatus === "ok") {
            ElementList.refresh();
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
