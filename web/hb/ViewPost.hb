<div id="ViewPost" class="container-full-bg" data-postid="{{mData.mId}}" data-userid="{{mData.mUserId}}">
    <div class="ViewPost-PostDetails">
        <div class="panel-heading">
            <h3 class="panel-title">Post Details</h3>
        </div>
        <div class="sidePadding container-fluid">
            <div class="row">
                <form>
                    <div class="form-group">
                        <label for="title">Title</label>
                        <input value="{{mData.mSubject}}" type="text" class="form-control" id="ViewPost-title" placeholder="Title">
                    </div>
                    <div class="form-group">
                        <label for="content">Content</label>
                        <textarea class="form-control" id="ViewPost-message" rows="3">{{mData.mMessage}}</textarea>
                    </div>
                    <!-- Displaying the link-->
                    <div class="form-group">
                        <label for="Link">Link</label>
                        <p><a href={{mData.mLink}} target="_blank">{{mData.mLink}}</a></p>
                    </div>
                    <div class="form-group">
                        <label for="File">Copy Link Address for file</label>
                        <p><a href={{mData.mFileString}} target="_blank">Posted file</a></p>
                    </div>
                  <!-- value of location would be value={{mData.mLocation}} -->
                 <div id="map" class="map">
                      <label for="Location">Map of dinning options</label>
                 </div>
                  <div id="map" class="map"><div id="popup"></div></div>
                </form>
            </div>
            <div class="row1">
                <button id="ViewPost-saveChangeBtn" type="submit" class="btn btn-default">Save changes</button>
            </div>
            <!-- Need to fix formatting of submit button -->
        </div>
    </div>
</div>
