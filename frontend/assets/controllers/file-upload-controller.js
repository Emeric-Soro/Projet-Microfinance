import { Controller } from '@hotwired/stimulus';

export default class extends Controller {
    static targets = ['input', 'preview', 'name', 'size'];
    static values = { maxSize: { type: Number, default: 10485760 } };

    connect() {
        this.inputTarget.addEventListener('change', () => this.handleFiles());
    }

    handleFiles() {
        const file = this.inputTarget.files[0];
        if (!file) return;

        if (file.size > this.maxSizeValue) {
            alert(`Le fichier dépasse la taille maximale de ${this.formatSize(this.maxSizeValue)}`);
            this.inputTarget.value = '';
            return;
        }

        if (this.hasNameTarget) {
            this.nameTarget.textContent = file.name;
        }
        if (this.hasSizeTarget) {
            this.sizeTarget.textContent = this.formatSize(file.size);
        }

        if (this.hasPreviewTarget && file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = (e) => {
                this.previewTarget.src = e.target.result;
                this.previewTarget.classList.remove('hidden');
            };
            reader.readAsDataURL(file);
        }
    }

    formatSize(bytes) {
        if (bytes < 1024) return bytes + ' o';
        if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' Ko';
        return (bytes / 1048576).toFixed(1) + ' Mo';
    }
}
