var describe: any;
var it: any;
var expect: any;
describe("My Jasmine Unit Test", function() {
    it("UI Test: Navbar loads add entry button", function(){
    	// click the button for showing the add button
	    $('#Navbar-add').click();
        // expect that the add form is not hidden
        expect($('#NewEntryForm').attr("role")).toEqual("dialog");
        $('#NewEntryForm-Close').click();  
    });
    /**
     * UI Test for if like increments
     */
    it("UI Test: Like button increments likes", function(){
        //Get number of likes before liking
        let before = parseInt($('.ElementList-numLikes').html());

        $('.ElementList-likebtn').click();

        //Get number of likes after liking
        let after = parseInt($('.ElementList-numLikes').html());

        //checks if likes go up
        expect(after).toEqual(before + 1);
    });
    /**
     * UI Test for link in post
     */
    it("UI Test: Link shows up properly", function(){
        expect($('.ViewPost-link').data('value')).toEqual('link'); 
    });
    /**
     * UI Test for if dislike increments
     */
    it("UI Test: Dislike button increments likes", function(){
        //Get number of dislikes before liking
        let before = parseInt($('.ElementList-numDislikes').html());

        $('.ElementList-likebtn').click();

        //Get number of likes after liking
        let after = parseInt($('.ElementList-numDislikes').html());

        //checks if likes go up
        expect(after).toEqual(before - 1);
    });
    /**
     * UI Test: Profile page
     */
    it("UI Test: Go to profile page and check that elements are there", function() {
        setTimeout(() => {
            return;
        }, 1000);

        $('#Navbar-goToProfile').click();
        console.log($('.MainProfilePage-username'));
        expect($('.MainProfilePage-username').data('value')).toEqual('Username');
        expect($('.MainProfilePage-email').data('value')).toEqual('Email');
        expect($('.MainProfilePage-comment').data('value')).toEqual('Comment');

        //Go back to home page
        $('#Navbar-homeBtn').click();

        setTimeout(() => {
            return;
        }, 1000);
    });
    /**
     * UI Test for if add adds
     */
    it("UI Test: add entry adds", function(){
    	// click the button for showing the add button
	    $('#Navbar-add').click();
        // expect that the add form is not hidden
        expect($('#NewEntryForm').attr("role")).toEqual("dialog");
        $('#NewEntryForm-OK').click();  
    });

    /**
     * UI Test for comments
     */

     it("UI Test: edit comment", function () {
        expect($('#CommentsList-editbtn').text()).toEqual('Edit');
        //UI test for link in comment
        expect($('#CommentList-link').data('value')).toEqual('link')

     });

    /*
     * UI Test for accessing location in post
     */
    it("UI Test: Link shows up properly", function(){
        expect($('.ViewPost-location').data('value')).toEqual('location'); 
    });
});
