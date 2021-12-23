<nav class="navbar navbar-default" id="Navbar">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <!-- Clicking the brand refreshes the page -->
            <a id="Navbar-homeBtn" class="navbar-brand" href="/">The Grinders</a>
        </div>
        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li>
                    <a class="btn btn-link" id="Navbar-addPost">
                        Add Entry
                        <span class="glyphicon glyphicon-plus"></span><span class="sr-only">Show Trending Posts</span>
                    </a>
                </li>
                <li>
                    <a class="btn btn-link" id="Navbar-addComment">
                        Add Comment
                        <span class="glyphicon glyphicon-plus"></span><span class="sr-only">Show Trending Posts</span>
                    </a>
                </li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">Profile<span class="caret"></span></a>
                    <ul class="dropdown-menu">
                    <li><a class="btn" id="Navbar-goToProfile" href="#">Profile Page</a></li>
                    <li><a class="btn" id="Navbar-signOut" href="#">Sign Out</a></li>
                    </ul>
                </li>
            </ul>
        </div> 
    </div>
</nav>