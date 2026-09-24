const formularioContacto = document.getElementById('formulario-contacto');
const telefono = document.getElementById('telefono');
const telefonoError = document.getElementById('telefono-error');
const respuestaFormulario = document.getElementById('respuesta-form');

function mostrarErrorTelefono() {
	telefonoError.textContent = 'El problema fue el teléfono: solo se permiten números.';
	telefonoError.style.display = 'block';
	telefono.setCustomValidity('El teléfono solo puede contener números.');
}

function limpiarErrorTelefono() {
	telefonoError.textContent = '';
	telefonoError.style.display = 'none';
	telefono.setCustomValidity('');
}

telefono.addEventListener('input', () => {
	const valorNumerico = telefono.value.replace(/[^0-9]/g, '');

	if (telefono.value !== valorNumerico) {
		telefono.value = valorNumerico;
		mostrarErrorTelefono();
	} else if (telefono.value) {
		limpiarErrorTelefono();
	}
});

telefono.addEventListener('invalid', () => {
	mostrarErrorTelefono();
});

formularioContacto.addEventListener('submit', (evento) => {
	if (!telefono.value || !/^[0-9]+$/.test(telefono.value)) {
		evento.preventDefault();
		mostrarErrorTelefono();
		respuestaFormulario.textContent = 'No se pudo enviar el formulario. El problema fue el teléfono: solo se permiten números.';
		telefono.focus();
	}
});
