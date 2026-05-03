import { Controller } from '@hotwired/stimulus';

export default class extends Controller {
    static targets = ['form'];
    static values = { url: String, method: { type: String, default: 'POST' } };

    async submit(event) {
        event.preventDefault();
        const form = this.formTarget || this.element;
        const formData = new FormData(form);
        const data = {};

        formData.forEach((value, key) => {
            const matches = key.match(/^([^\[]+)(?:\[(.+)\])?$/);
            if (matches) {
                if (matches[2]) {
                    if (!data[matches[1]]) data[matches[1]] = {};
                    data[matches[1]][matches[2]] = value;
                } else {
                    data[matches[1]] = value;
                }
            }
        });

        try {
            const response = await fetch(this.urlValue || form.action, {
                method: this.methodValue,
                headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
                body: JSON.stringify(data),
            });

            if (!response.ok) throw new Error('Request failed');

            const result = await response.json();
            this.dispatch('success', { detail: result });
        } catch (error) {
            this.dispatch('error', { detail: { message: error.message } });
        }
    }
}
