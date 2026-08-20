// ============================================================
//  CORVACHO'S - main.js
// ============================================================

//---Navbar scroll effect---
const navbar = document.getElementById('navbar');
window.addEventListener('scroll', () => {
  if (window.scrollY > 60) {
    navbar.classList.add('scrolled');
  } else {
    navbar.classList.remove('scrolled');
  }
});

//---Hamburger menu---
const hamburger = document.getElementById('hamburger');
const navLinks  = document.getElementById('navLinks');

hamburger.addEventListener('click', () => {
  navLinks.classList.toggle('open');
  const isOpen = navLinks.classList.contains('open');
  hamburger.setAttribute('aria-expanded', isOpen);
});

// Close menu when a link is clicked (mobile)
document.querySelectorAll('.nav-link').forEach(link => {
  link.addEventListener('click', () => {
    navLinks.classList.remove('open');
  });
});

const revealEls = document.querySelectorAll(
  '.feature-card, .menu-card, .footer-col, .section-title, .section-badge, .section-desc'
);

const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.style.opacity = '1';
      entry.target.style.transform = 'translateY(0)';
      observer.unobserve(entry.target);
    }
  });
}, { threshold: 0.12 });

revealEls.forEach(el => {
  el.style.opacity = '0';
  el.style.transform = 'translateY(28px)';
  el.style.transition = 'opacity 0.7s ease, transform 0.7s ease';
  observer.observe(el);
});

// ============================================================
//  FORMULARIO DE CONTACTO
// ============================================================

(function () {
  const form      = document.getElementById('contactForm');
  if (!form) return;

  const submitBtn  = document.getElementById('cfSubmitBtn');
  const successBox = document.getElementById('cfSuccess');

  // Campos requeridos con su mensaje de error
  const requiredFields = [
    { id: 'cfFirstName', errId: 'errFirstName', msg: 'El nombre es obligatorio.' },
    { id: 'cfLastName',  errId: 'errLastName',  msg: 'El apellido es obligatorio.' },
    { id: 'cfEmail',     errId: 'errEmail',     msg: 'Introduce un correo válido.' },
    { id: 'cfSubject',   errId: 'errSubject',   msg: 'El asunto es obligatorio.' },
    { id: 'cfMessage',   errId: 'errMessage',   msg: 'El mensaje es obligatorio.' },
  ];

  // Validar un campo individual → devuelve true si es válido
  function validateField(fieldDef) {
    const el  = document.getElementById(fieldDef.id);
    const err = document.getElementById(fieldDef.errId);
    let valid = true;

    if (fieldDef.id === 'cfEmail') {
      const emailOk = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(el.value.trim());
      valid = emailOk;
    } else {
      valid = el.value.trim() !== '';
    }

    if (!valid) {
      el.classList.add('cf-invalid');
      err.textContent = fieldDef.msg;
    } else {
      el.classList.remove('cf-invalid');
      err.textContent = '';
    }
    return valid;
  }

  // Validación en tiempo real al salir del campo (blur)
  requiredFields.forEach(def => {
    const el = document.getElementById(def.id);
    if (el) el.addEventListener('blur', () => validateField(def));
    if (el) el.addEventListener('input', () => {
      // Quitar error tan pronto empiece a escribir
      el.classList.remove('cf-invalid');
      document.getElementById(def.errId).textContent = '';
    });
  });

  // Envío del formulario
  form.addEventListener('submit', function (e) {
    e.preventDefault();

    // Validar todos los campos
    let allValid = true;
    requiredFields.forEach(def => {
      if (!validateField(def)) allValid = false;
    });

    if (!allValid) return;

    // Estado de carga
    submitBtn.classList.add('loading');
    submitBtn.disabled = true;
    successBox.classList.remove('visible');
    successBox.textContent = '';

    // Simular envío (1.8 s)
    setTimeout(() => {
      submitBtn.classList.remove('loading');
      submitBtn.disabled = false;
      form.reset();
      successBox.textContent = '¡Mensaje enviado correctamente! Nos pondremos en contacto contigo pronto!';
      successBox.classList.add('visible');

      // Ocultar mensaje tras 6 s
      setTimeout(() => successBox.classList.remove('visible'), 6000);
    }, 1800);
  });
})();
