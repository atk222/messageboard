<!-- NewEntryFrom Code Pasted here -->
<div id="NewEntryForm" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Add a New Entry</h4>
            </div>
            <div class="modal-body">
                <!--Title stuff-->
                <label for="NewEntryForm-title">Title</label>
                <input class="form-control" type="text" id="NewEntryForm-title" />
                <!--Message Stuff-->
                <label for="NewEntryForm-message">Message</label>
                <textarea class="form-control" type="text" id="NewEntryForm-message"></textarea>
                <!--Link stuff-->
                <label for="NewEntryForm-link">Enter Link </label>
                <textarea class="form-control" type="text" id="NewEntryForm-link" /></textarea>
                <!--file stuff-->
                <label for="NewEntryForm-file">Upload file</label>
                <form>
                    <input id="NewEntryForm-file" type="file" name="file"/>
                <form> 
                <!--location stuff-->
                <label for="NewEntryForm-location">Select Location</label>
                    <select id="NewEntryForm-location">
                        <option value = "nothing"></option>
                        <option value = "Rathbone Hall">Rathbone Hall</option>
                        <option value = "Lower/Upper Court">Lower/Upper Court</option>
                        <option value = "Common Grounds Cafe">Common Grounds Cafe</option>
                        <option value = "FML The Grind Cafe">FML The Grind Cafe</option>
                        <option value = "Williams Global Cafe">Williams Global Cafe</option>
                        <option value = "Lucy's Cafe">Lucy's Cafe</option>
                    </select>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" id="NewEntryForm-OK">OK</button>
                <button type="button" class="btn btn-default" id="NewEntryForm-Close">Close</button>
            </div>
        </div>
    </div>
</div>