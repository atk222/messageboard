class ViewPost {
    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    public static currentPostInfo;

    /**
    * The name of the DOM entry associated with Navbar
    */
    private static readonly NAME = "ViewPost";

    /**
     * Check if singleton is initialized yet
     */
    private static init() {
        if (!ViewPost.isInit) {
            ViewPost.isInit = true;
        }
    }

    public static removeViewPostPage() {
        $("#" + ViewPost.NAME).remove();
        ViewPost.isInit = false;
    }

    /**
     * Load UI with userProfile info
     */
    public static refresh(postInfo) {
        console.log("Refresh ViewPost");
       
        console.log(postInfo.mData.mLocation);
        console.log(postInfo.mData.mCoords[1]);
        console.log(postInfo.mData.mCoords[0]);

        



        //Remove current ViewPost elements
        ViewPost.removeViewPostPage();

        $("body").append(Handlebars.templates[ViewPost.NAME + ".hb"](postInfo));
        //Show add comment button when viewing post
        $("#" + Navbar.NAME + "-addComment").show();
        //Hide the add post button
        $("#" + Navbar.NAME + "-addPost").hide();


        // CommentsList.refresh(sampleComment);
        CommentsList.refresh(postInfo);

        $("#" + ViewPost.NAME + "-saveChangeBtn").click(ViewPost.submitPostChange);

        //Check if post is made by user and then hide save changes button if they are different
        let currentUserID = localStorage.getItem('userID');
        if (currentUserID !== postInfo.mData.mUserId) {
            $("#" + ViewPost.NAME + "-saveChangeBtn").hide();
        }

        //create a map that is centered on the coordinates specified 
        var map = new ol.Map({
            target: 'map',
            layers: [
            new ol.layer.Tile({
                source: new ol.source.OSM()
            })
            ],
            view: new ol.View({
            center: ol.proj.fromLonLat([postInfo.mData.mCoords[1], postInfo.mData.mCoords[0]]),
            zoom: 18
            
            })
        });
        
        //create a marker at the coordinates given
        var layer2 = new ol.layer.Vector({
              source: new ol.source.Vector({
                  features: [
                     new ol.Feature({
                         geometry: new ol.geom.Point(ol.proj.fromLonLat([postInfo.mData.mCoords[1], postInfo.mData.mCoords[0]])),
                     })
                ]
             })
        });

            map.addLayer(layer2); 
           
        //create a popup that shows the name of the location when you click on the marker
        var element = document.getElementById('popup');

        var popup = new ol.Overlay({
        element: element,
        positioning: 'bottom-center',
        stopEvent: false,
        offset: [0, -50]
        });
        map.addOverlay(popup);

        map.on('click', function(e){
           var feature =  map.forEachFeatureAtPixel(e.pixel, function(feature){
                return feature;
            });
            if (feature) {
                var coordinates = feature.getGeometry().getCoordinates();
                popup.setPosition(coordinates);
                $(element).popover({
                  placement: 'top',
                  html: true,
                  content: postInfo.mData.mLocation
                });
                $(element).popover('show');
              } else {
                $(element).popover('destroy');
              }
            });

        //Store new postInfo so that other parts of ViewPost can use it if needed
        //When refreshing, update with up to date info
        ViewPost.currentPostInfo = postInfo;
    }

    /**
     * For submitting changes to the message/post
     */
    private static submitPostChange() {
        console.log("submitPostChange clicked");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let title = "" + $("#" + ViewPost.NAME + "-title").val();
        let msg = "" + $("#" + ViewPost.NAME + "-message").val();
        let link= "" + $("#" +ViewPost.NAME + "-link").val();

        


        // NB: we assume that the user didn't modify the value of #editId
        let id = "" + $("#editId").val();
        
        if (title === "" || msg === "") {
            window.alert("Error: title or message is not valid");
            return;
        }
        
        // set up an AJAX post.  When the server replies, the result will go to
        // onSubmitResponse
        $.ajax({
            type: "PUT",
            url: backendUrl + "/messages/" + ViewPost.currentPostInfo.mData.mId,
            dataType: "json",
            data: JSON.stringify({ mTitle: title, mMessage: msg, mLink: link}),
            success: ViewPost.onSubmitResponse
        });

    }
    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a
     * result.
     * 
     * @param data The object returned by the server
     */
    private static onSubmitResponse(data: any) {
        console.log("onSubmitResponse pressed");
        // If we get an "ok" message, clear the form and refresh the main
        // listing of messages
        if (data.mStatus === "ok") {
            $.ajax({
                type: "GET",
                url: backendUrl + "/messages/" + ViewPost.currentPostInfo.mData.mId,
                dataType: "json",
                // TODO: we should really have a function that looks at the return
                //       value and possibly prints an error message.
                success: ViewPost.refresh
            });
        }
       // console.log(data);
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