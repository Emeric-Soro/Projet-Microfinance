import { Controller } from '@hotwired/stimulus';

export default class extends Controller {
    static targets = ['input'];
    static values = { locale: { type: String, default: 'fr-FR' } };

    connect() {
        const input = this.inputTarget;
        input.type = 'date';
    }
}
