import { Controller } from '@hotwired/stimulus';

export default class extends Controller {
    static targets = ['tab', 'panel'];

    connect() {
        if (!this.hasTabTarget) return;
        // Activate first tab by default
        this.switch(this.tabTargets[0]);
    }

    switch(event) {
        const tab = event.currentTarget || event;
        const target = typeof tab === 'string' ? tab : tab.dataset.tab;

        this.tabTargets.forEach(t => {
            const isActive = t.dataset.tab === target;
            t.classList.toggle('border-primary-500', isActive);
            t.classList.toggle('text-primary-500', isActive);
            t.classList.toggle('text-gray-500', !isActive);
            t.classList.toggle('border-transparent', !isActive);
            t.setAttribute('aria-selected', isActive);
        });

        this.panelTargets.forEach(p => {
            p.classList.toggle('hidden', p.id !== `tab-${target}`);
        });
    }

    switchTab(event) {
        this.switch(event);
    }
}
