import './css/app.css';
import { startStimulusApp } from '@symfony/stimulus-bridge';

// Starts all Stimulus controllers in assets/controllers/
const app = startStimulusApp(require.context(
    '@symfony/stimulus-bridge/lazy-controller-loader!./controllers',
    true,
    /\.[jt]sx?$/
));

// Turbo support
import '@hotwired/turbo';

export { app };
