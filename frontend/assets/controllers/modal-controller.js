import { Controller } from '@hotwired/stimulus';

export default class extends Controller {
    static targets = ['dialog', 'content'];
    static values = { open: { type: Boolean, default: false } };

    open() {
        this.openValue = true;
        this.dialogTarget.classList.remove('hidden');
        this.dialogTarget.classList.add('flex');
        document.body.classList.add('overflow-hidden');
    }

    close() {
        this.openValue = false;
        this.dialogTarget.classList.add('hidden');
        this.dialogTarget.classList.remove('flex');
        document.body.classList.remove('overflow-hidden');
    }

    closeOutside(event) {
        if (event.target === this.dialogTarget) {
            this.close();
        }
    }

    closeWithEscape(event) {
        if (event.key === 'Escape') {
            this.close();
        }
    }
}
