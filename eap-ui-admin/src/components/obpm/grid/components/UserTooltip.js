class UserTooltip {
    init(params) {
        const eGui = (this.eGui = document.createElement("div"));
        const color = params.color || "white";
        const data = params.api.getDisplayedRowAtIndex(params.rowIndex).data;
        eGui.classList.add("custom-tooltip");
        //@ts-ignore
        eGui.style["background-color"] = color;
        eGui.innerHTML = `
        <p>
                <span class="name">${data.username}</span>
            </p>
            <p>
                <span>职务: </span>
                ${data.username}
            </p>
            <p>
                <span>电话: </span>
                ${data.phone}
        </p>
        `;
    }
    getGui() {
        return this.eGui;
    }
}