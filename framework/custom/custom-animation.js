var Animation = {
    right: function(id) {
        console.log(id);
        $(this).animate({ "left": "+=50px" }, "slow" );
    },
    left: function(id) {
        console.log(id);
        $(this).animate({ "left": "-=50px" }, "slow" );
    }
    /**
     * Created by KIMSEONHO on 14. 2. 13.
     */
};
