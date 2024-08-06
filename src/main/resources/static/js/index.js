document.addEventListener('DOMContentLoaded', function () {
    const floatingDiv = document.getElementById('floating-div');
    const showDivThreshold = 0.2; // 20% of the page height

    function checkScroll() {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        const viewportHeight = window.innerHeight;
        const documentHeight = document.documentElement.scrollHeight;

        if (scrollTop <= documentHeight * showDivThreshold) {
            floatingDiv.classList.add('show');
        } else {
            floatingDiv.classList.remove('show');
        }
    }

    // Initial check on page load
    checkScroll();

    // Check scroll position on scroll events
    window.addEventListener('scroll', checkScroll);
});