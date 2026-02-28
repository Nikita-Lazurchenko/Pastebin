document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('registrationForm');
    const submitBtn = document.getElementById('submitBtn');

    function setElementError(inputId, errorId, message, isValid) {
        const input = document.getElementById(inputId);
        const errorSpan = document.getElementById(errorId);

        if (isValid) {
            input.style.borderColor = "#2ecc71";
            errorSpan.textContent = "";
            errorSpan.style.display = "none";
        } else {
            input.style.borderColor = "#e74c3c";
            errorSpan.textContent = message;
            errorSpan.style.display = "block";
            errorSpan.style.color = "#e74c3c";
            errorSpan.style.fontSize = "12px";
        }
    }

    function validateForm() {
        const username = document.getElementById('username');
        const email = document.getElementById('email');
        const password = document.getElementById('password');
        const confirm = document.getElementById('confirm-password');

        const usernameRegex = /^[a-zA-Z0-9_]{3,}$/;
        const isUsernameValid = usernameRegex.test(username.value);
        setElementError('username', 'usernameError', "От 3 символов (латиница, цифры, _)", isUsernameValid);

        const isEmailValid = email.checkValidity();
        setElementError('email', 'emailError', "Введите корректный email", isEmailValid);

        const isPassLongEnough = password.value.length >= 6;
        setElementError('password', 'passwordError', "Минимум 6 символов", isPassLongEnough);

        const doMatch = password.value === confirm.value && confirm.value !== "";
        setElementError('confirm-password', 'confirmError', "Пароли не совпадают", doMatch);

        const isFormValid = isUsernameValid && isEmailValid && isPassLongEnough && doMatch;
        submitBtn.disabled = !isFormValid;
        submitBtn.style.opacity = isFormValid ? "1" : "0.5";
        submitBtn.style.cursor = isFormValid ? "pointer" : "not-allowed";
    }

    form.addEventListener('input', validateForm);
});