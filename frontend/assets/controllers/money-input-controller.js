import { Controller } from '@hotwired/stimulus';

export default class extends Controller {
    static targets = ['input'];
    static values = { precision: { type: Number, default: 0 } };

    connect() {
        const input = this.inputTarget;
        if (this.precisionValue > 0) {
            input.step = Math.pow(10, -this.precisionValue).toFixed(this.precisionValue);
        }
        input.classList.add('tabular-nums');
        input.type = 'text';
        input.inputMode = 'decimal';

        input.addEventListener('blur', () => this.formatDisplay());
        input.addEventListener('focus', () => this.removeFormatting());
    }

    formatDisplay() {
        const input = this.inputTarget;
        const value = parseFloat(input.value.replace(/[^\d,-]/g, '').replace(',', '.'));
        if (!isNaN(value)) {
            input.value = value.toLocaleString(this.localeValue, {
                minimumFractionDigits: this.precisionValue,
                maximumFractionDigits: this.precisionValue,
            });
        }
    }

    removeFormatting() {
        const input = this.inputTarget;
        const value = parseFloat(input.value.replace(/[^\d,-]/g, '').replace(',', '.'));
        if (!isNaN(value)) {
            input.value = value.toFixed(this.precisionValue);
        }
    }
}
