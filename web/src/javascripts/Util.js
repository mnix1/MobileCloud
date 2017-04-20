const Util = {
    units: ["B", "KB", "MB", "GB", "TB", "PB"],
    sizeToReadableUnit: function (sizeBytes) {
        var index = 0;
        if (sizeBytes == null) {
            return "0 " + this.units[index];
        }
        var size = sizeBytes;
        while (size > 1024 && index < this.units.length) {
            index++;
            size /= 1024;
        }
        return this.round(size, 2) + " " + this.units[index];
    },
    round: function (value, exp) {
        if (typeof exp === 'undefined' || +exp === 0)
            return Math.round(value);

        value = +value;
        exp = +exp;

        if (isNaN(value) || !(typeof exp === 'number' && exp % 1 === 0))
            return NaN;

        // Shift
        value = value.toString().split('e');
        value = Math.round(+(value[0] + 'e' + (value[1] ? (+value[1] + exp) : exp)));

        // Shift back
        value = value.toString().split('e');
        return +(value[0] + 'e' + (value[1] ? (+value[1] - exp) : -exp));
    }
};

export default Util;