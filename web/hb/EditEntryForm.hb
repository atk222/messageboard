<!-- EditEntryForm Code Pasted here -->
<div id="EditEntryForm" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Edit Comment</h4>
            </div>
            <div class="modal-body">
                <label for="EditEntryForm-comment">Comment</label>
                <textarea class="form-control" id="EditEntryForm-comment"></textarea>
            </div>
            <div class="form-group">
                    <laberl for="link">Link</label>
                    <textarea class="form-control" id="EditEntryForm-link" rows="1"></textarea>
            </div>
            {{! Check if user id matches user id of post of to check if user can edit}}
            <div class="modal-footer">
                <button type="button" class="btn btn-default" id="EditEntryForm-saveChanges">Save changes</button>
                <button type="button" class="btn btn-default" id="EditEntryForm-Close">Close</button>
            </div>
        </div>
    </div>
</div>