<div class="panel panel-default" id="ElementList">    
    <div class="panel-heading">
        <h3 class="panel-title">Messages</h3>
    </div>
    <table class="table table-hover">
        <tbody>
            {{#each mData}}
            <tr id="ElementList-post" class="ElementList-commentBtn" data-userid="{{this.mUserId}}" data-value="{{this.mId}}"><a href="#">
                <td><button class="ElementList-image btn btn-default"><img src="{{mImageURL}}" alt="profileImage" class="img-circle" style="margin-bottom:10px"></button></td>
                <td>{{this.mSubject}}</td>
                <td>{{this.mMessage}}</td>
                <td><button class="ElementList-likebtn btn btn-default" data-value="{{this.mId}}">😀</button></td>
                <td class="ElementList-numLikes">{{this.mLikes}}</td>
                <td><button class="ElementList-dlikebtn btn btn-default" data-value="{{this.mId}}">☹</button></td>
                <td class="ElementList-numDislikes">{{this.mDislikes}}</td>
                <!--<td><button class="ElementList-editbtn btn btn-default" data-value="{{this.mId}}">Edit</button></td>-->
                <td><button class="ElementList-delbtn btn btn-default" data-value="{{this.mId}}">Delete</button></td>
            </a></tr>
            {{/each}}
        </tbody>
    </table>
</div>