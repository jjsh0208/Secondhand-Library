document.addEventListener('DOMContentLoaded', () => {
    const profileButton = document.querySelector('.profile-button');
    const profile = document.querySelector('.profile');

    profileButton.addEventListener('click', () => {
        profile.classList.toggle('show');
    });
});