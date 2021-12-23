<div id="CommentsList-commentsSection">
    <div class="panel-heading">
        <h3 class="panel-title">Comments</h3>
    </div>
    <div class="container-fluid">
    {{#each mData}}
        <div class="row CommentsList-entry" data-commentid="{{this.mCommentID}}" data-userid="{{this.mUserID}}" data-postid="{{this.mPostID}}">
            <div class="col-xs-6 col-sm-1">
                <a class="CommentsList-image"><img src="{{this.mImageURL}}" alt="profileImage" class="img-circle"></a>
            </div>
            <div class="Comment-textarea col-xs-6 col-sm-10">
                <form>
                    <div class="form-group">
                        <label for="userName">{{this.mUsername}}</label>
                        <textarea class="form-control" id="CommentsList-comment" rows="4" readonly>{{this.mComment}}</textarea>
                    </div>
                    <div class="form-group">
                        <p><a href={{mData.mLink}}>Posted Link</a></p>
                    </div>
                </form>
            </div>

            <!-- Add the extra clearfix for only the required viewport -->
            <div class="clearfix visible-xs-block"></div>

            <div class="CommentsList-editbtn col-xs-6 col-sm-1">
                <button class="btn btn-default" data-commentid-edit="{{this.mCommentID}}" data-postid="{{this.mPostID}}">Edit</button>
            </div>
        </div>
    {{/each}}

    </div>
</div>