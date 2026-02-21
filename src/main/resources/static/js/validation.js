document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('registrationForm');
    const submitBtn = document.getElementById('submitBtn');

    function setElementError(inputId, errorId, message, isValid) {
        const input = document.getElementById(inputId);
        const errorSpan = document.getElementById(errorId);

        if (isValid) {
            input.style.borderColor = "#2ecc71"; // Зеленый
            errorSpan.textContent = "";
            errorSpan.style.display = "none";
        } else {
            input.style.borderColor = "#e74c3c"; // Красный
            errorSpan.textContent = message;
            errorSpan.style.display = "block";
            errorSpan.style.color = "#e74c3c";
            errorSpan.style.fontSize = "12px";
        }
    }

    function validateForm() {
        const email = document.getElementById('email');
        const password = document.getElementById('password');
        const confirm = document.getElementById('confirm-password');

        const isEmailValid = email.checkValidity();
        setElementError('email', 'emailError', "Введите корректный email", isEmailValid);

        const isPassLongEnough = password.value.length >= 6;
        setElementError('password', 'passwordError', "Минимум 6 символов", isPassLongEnough);

        const doMatch = password.value === confirm.value && confirm.value !== "";
        setElementError('confirm-password', 'confirmError', "Пароли не совпадают", doMatch);

        submitBtn.disabled = !(isEmailValid && isPassLongEnough && doMatch);
        submitBtn.style.opacity = submitBtn.disabled ? "0.5" : "1";
        submitBtn.style.cursor = submitBtn.disabled ? "not-allowed" : "pointer";
    }

    form.addEventListener('input', validateForm);
});