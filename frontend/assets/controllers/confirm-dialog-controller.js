import { Controller } from '@hotwired/stimulus';

export default class extends Controller {
    static targets = ['dialog'];
    static values = { url: String };

    confirm(event) {
        if (this.urlValue) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = this.urlValue;
            form.style.display = 'none';
            document.body.appendChild(form);
            form.submit();
        }
    }

    cancel() {
        this.dialogTarget.classList.add('hidden');
        this.dialogTarget.classList.remove('flex');
        document.body.classList.remove('overflow-hidden');
    }

    closeWithEscape(event) {
        if (event.key === 'Escape') {
            this.cancel();
        }
    }
}
