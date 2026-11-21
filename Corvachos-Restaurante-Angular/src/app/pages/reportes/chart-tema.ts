import {
  BarController, BarElement, CategoryScale, Chart, Filler, LineController, LineElement,
  LinearScale, PointElement, Tooltip
} from 'chart.js';

// Registra solo las piezas de Chart.js que usa el dashboard (asi el bundle queda mas liviano)
Chart.register(LineController, LineElement, PointElement, BarController, BarElement,
  CategoryScale, LinearScale, Filler, Tooltip);

// Colores del tema del restaurante para todas las graficas
export const TEMA = {
  oro: '#cd9e46',
  oroSuave: 'rgba(205, 158, 70, 0.35)',
  texto: '#fcfafa',
  textoSuave: '#9fb3bf',
  rejilla: 'rgba(255, 255, 255, 0.06)',
  tooltipFondo: '#06141d'
};

Chart.defaults.color = TEMA.textoSuave;
Chart.defaults.font.family = "'Montserrat', sans-serif";
Chart.defaults.font.size = 11;

// Estilo comun del tooltip
export const TOOLTIP_BASE = {
  backgroundColor: TEMA.tooltipFondo,
  borderColor: TEMA.oroSuave,
  borderWidth: 1,
  padding: 12,
  cornerRadius: 8,
  titleColor: TEMA.texto,
  bodyColor: TEMA.texto,
  titleFont: { weight: 600 as const },
  displayColors: false
};

export { Chart };
