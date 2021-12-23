<div id="MainProfilePage" class="container-full-bg">
    <div class="panel-heading">
        <h3 class="panel-title">Profile</h3>
    </div>
    <div class="sidePadding container-fluid">
        <div class="row">
            <img src="{{uImageURL}}" alt="profileImage" class="img-circle" style="margin-bottom:10px">
        </div>
        
        <div class="row">
            <form>
                <div class="form-group">
                    <label class="MainProfilePage-username" data-value="Username" for="exampleFormControlInput1">Username</label>
                    <input value="{{uName}}" type="email" class="form-control" id="MainProfilePage-userName" placeholder="username" readonly>
                </div>
                <div class="form-group">
                    <label class="MainProfilePage-email" data-value="Email" for="exampleFormControlSelect1">Email</label>
                    <input value="{{uEmail}}" type="email" class="form-control" id="MainProfilePage-userEmail" placeholder="email@lehigh.edu" readonly>
                </div>
                <div class="form-group">
                    <label class="MainProfilePage-comment" data-value="Comment" for="exampleFormControlTextarea1">Comment</label>
                    <textarea class="form-control" id="MainProfilePage-userComment" rows="3">{{uComment}}</textarea>
                </div>
            </form>
        </div>
        {{! Need to check if user id matches the user_id of profile page to check if user can edit their comment or not}}
        <div class="row">
            <button id="MainProfilePage-submitBtn" type="submit" class="btn btn-default">Save comment change</button>
        </div>
        <!-- Need to fix formatting of submit button -->
    </div>
</div>