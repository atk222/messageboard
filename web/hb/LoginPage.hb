<div id="LoginPage" class="container-full-bg vertical-center">
    <div class="container-fluid">
        <div class="column center-block">
            <h1>TheGrinders</h1>
            <div class="g-signin2" data-onsuccess="onSignIn"></div>
            <script>
                function onSignIn(googleUser) {
                    LoginPage.onSignIn(googleUser);
                }
            </script>
            <div id="my-signin2"></div>
            <script src="https://apis.google.com/js/platform.js?onload=renderButton" async defer></script>
        </div>
    </div>
</div>
