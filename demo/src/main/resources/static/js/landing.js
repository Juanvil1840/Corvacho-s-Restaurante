// Validacion y envio del formulario de contacto
document.getElementById('formulario-contacto').addEventListener('submit', function(e) {
    e.preventDefault();

    var nombre   = document.getElementById('nombre').value.trim();
    var apellido = document.getElementById('apellido').value.trim();
    var correo   = document.getElementById('correo').value.trim();
    var telefono = document.getElementById('telefono').value.trim();
    var asunto   = document.getElementById('asunto').value.trim();
    var mensaje  = document.getElementById('mensaje').value.trim();
    var respuesta = document.getElementById('respuesta-form');

    // Validar que todos los campos esten llenos
    if (!nombre || !apellido || !correo || !telefono || !asunto || !mensaje) {
        respuesta.textContent = 'Por favor, completa todos los campos.';
        return;
    }

    // Validar formato de correo
    var regexCorreo = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regexCorreo.test(correo)) {
        respuesta.textContent = 'Por favor, ingresa un correo electronico valido.';
        return;
    }

    // Si todo esta bien
    respuesta.textContent = 'Mensaje enviado correctamente';
    this.reset();
});
