var d3 = require("d3");

var d3_wrapper = {

    width: "100%",
    height: "896px",
    svg: null,
    svgCellGroup: null,
    svgUnitGroup: null,
    cells: null,
    rows: 28,
    cols: 18,
    size: 32,
    selectedUnitSvg: null,
    selectedUnitBuiltSvg: null,

    init: function () {
        this.cells = d3.range(0, this.rows * this.cols).map(function (d) {
            var col = d % d3_wrapper.cols;
            var row = (d - col) / d3_wrapper.cols;
            return {
                row: row,
                col: col,
                x: col * d3_wrapper.size,
                y: row * d3_wrapper.size
            };
        });
    },

    iconPath: function (iconPath) {
        return LegionTD2Builder.iconsPath + iconPath.replace("Splashes", "");
    },

    render: function (id, selectedUnit, units, buildUnitCallback, selectUnitCallback) {
        var that = this;
        var area = d3.select("#" + id);
        // @todo: hack to wait till react rendered
        if (area.size() === 0) {
            setTimeout(function () {
                d3_wrapper.render(id, selectedUnit, units, buildUnitCallback, selectUnitCallback);
            }, 20);
            return;
        }

        /**
         * Init svg element (first time or switch between wave editor / build order)
         */
        if (this.svg === null || area.select("#svg").size() === 0) {
            this.svg = area.append("svg")
                .attr("id", "svg")
                .attr("width", this.width)
                .attr("height", this.height)
                .attr("preserveAspectRatio", "xMidYMid meet")
                .attr("viewBox", "0 0 " + (this.size * this.cols) + " " + (this.size * this.rows));

            area.on("mouseleave", function (d) {
                if (d3_wrapper.selectedUnitSvg) {
                    d3_wrapper.selectedUnitSvg
                        .attr("x", -d3_wrapper.size * 2)
                        .attr("y", -d3_wrapper.size * 2);
                }
            });
            this.selectedUnitSvg = null;
            this.selectedUnitBuiltSvg = null;
            this.svgCellGroup = this.svg.append("g")
                .attr("id", "cells");
            this.svgUnitGroup = this.svg.append("g")
                .attr("id", "units");
        }

        /**
         * Paint unit to build
         */
        if (selectedUnit.isNewUnit()) {
            this.selectedUnitSvg = this.svg
                .selectAll(".newUnit")
                .data([selectedUnit.getNewUnit()])
                .attr("x", -d3_wrapper.size * 2)
                .attr("y", -d3_wrapper.size * 2)
                .attr("href", function (d) {
                    return that.iconPath(d.iconPath)
                });
            this.selectedUnitSvg.enter()
                .append("image")
                .attr("class", "newUnit")
                .attr("x", -d3_wrapper.size * 2)
                .attr("y", -d3_wrapper.size * 2)
                .attr("width", d3_wrapper.size * 2)
                .attr("height", d3_wrapper.size * 2)
                .attr("href", function (d) {
                    return that.iconPath(d.iconPath)
                })
                .on("click", function (d) {
                    var unitSvg = d3.select(this),
                        x = unitSvg.attr("x") / d3_wrapper.size,
                        y = unitSvg.attr("y") / d3_wrapper.size;
                    // @todo: remove if translation is correct in replay load
                    if (d3_wrapper.svgUnitGroup && d3_wrapper.svgUnitGroup.attr("transform")) {
                        var match = d3_wrapper.svgUnitGroup.attr("transform").match(/\(-([\d]+), -([\d]+)\)/);
                        x = match[1] / d3_wrapper.size - x - 2;
                        y = match[2] / d3_wrapper.size - y - 2;
                    }
                    buildUnitCallback(d, x, y);
                })
                .on("contextmenu", function (d) {
                    d3.event.preventDefault();
                    if (d3_wrapper.selectedUnitSvg) {
                        d3_wrapper.selectedUnitSvg.remove();
                    }
                });
            this.selectedUnitSvg.exit()
                .remove();
            this.selectedUnitSvg = this.svg.selectAll(".newUnit");
        }
        else if (this.selectedUnitSvg) {
            this.selectedUnitSvg.remove();
        }

        /**
         * Paint cells
         */
        this.svgCellGroup
            .selectAll(".cell")
            .data(this.cells)
            .enter()
            .append("rect")
            .attr("class", "cell")
            .attr("x", function (d) {
                return d.x;
            })
            .attr("y", function (d) {
                return d.y;
            })
            .attr("width", d3_wrapper.size)
            .attr("height", d3_wrapper.size)
            .each(function (d) {
                d.element = d3.select(this);
            })
            .on("mouseover", function (d) {
                if (d3_wrapper.selectedUnitSvg && d3_wrapper.validPlacement(d)) {
                    d3_wrapper.selectedUnitSvg
                        .attr("x", d.x)
                        .attr("y", d.y);
                }
            })
            .on("contextmenu", function (d) {
                d3.event.preventDefault();
                if (d3_wrapper.selectedUnitBuiltSvg) {
                    d3_wrapper.selectedUnitBuiltSvg.remove();
                    selectUnitCallback(selectedUnit.getBuiltUnit());
                }
            });


        /**
         * Paint Units
         */
        var unitsSvg = this.svgUnitGroup
            .selectAll(".unit")
            .data(units)
            .attr("x", function (d) {
                return d.position.x * d3_wrapper.size;
            })
            .attr("y", function (d) {
                return d.position.y * d3_wrapper.size;
            })
            .attr("href", function (d) {
                return that.iconPath(d.def.iconPath)
            });

        unitsSvg.enter()
            .append("image")
            .attr("class", "unit")
            .attr("x", function (d) {
                return d.position.x * d3_wrapper.size;
            })
            .attr("y", function (d) {
                return d.position.y * d3_wrapper.size;
            })
            .attr("width", d3_wrapper.size * 2)
            .attr("height", d3_wrapper.size * 2)
            .attr("href", function (d) {
                return that.iconPath(d.def.iconPath)
            })
            .on("click", function (d) {
                selectUnitCallback(d);
            });

        unitsSvg.exit()
            .remove();

        if (units.length === 0) {
            this.svgUnitGroup
                .selectAll(".unit")
                .remove();
            this.svgUnitGroup.attr("transform", null);
        }
        else {
            // @todo: fix translate in replay reader
            var maxX = 0;
            units.forEach(function (value) {
                maxX = Math.max(value.position.x, maxX)
            });
            if (maxX > 19 && maxX <= 33) {
                this.svgUnitGroup.attr("transform", "rotate(180) translate(-1120, -5888)");
            }
            else if (maxX >= 57 && maxX <= 73) {
                this.svgUnitGroup.attr("transform", "rotate(180) translate(-2400, -5888)");
            }
            else if (maxX >= 89 && maxX <= 105) {
                this.svgUnitGroup.attr("transform", "rotate(180) translate(-3424, -5888)");
            }
            else if (maxX >= 125 && maxX <= 141) {
                this.svgUnitGroup.attr("transform", "rotate(180) translate(-4576, -5888)");
            }
            else if (maxX >= 189 && maxX <= 205) {
                this.svgUnitGroup.attr("transform", "rotate(180) translate(-6624, -5888)");
            }
            else if (maxX >= 225 && maxX <= 241) {
                this.svgUnitGroup.attr("transform", "rotate(180) translate(-7776, -5888)");
            }
            else if (maxX >= 261 && maxX <= 277) {
                this.svgUnitGroup.attr("transform", "rotate(180) translate(-8928, -5888)");
            }
            else if (maxX >= 297 && maxX <= 313) {
                this.svgUnitGroup.attr("transform", "rotate(180) translate(-10080, -5888)");
            }
            else {
                this.svgUnitGroup.attr("transform", null);
            }
        }

        /**
         * Paint marker for selected unit
         */
        if (selectedUnit.isBuiltUnit()) {
            this.selectedUnitBuiltSvg = this.svgUnitGroup
                .selectAll(".unit.selected")
                .data([selectedUnit.getBuiltUnit()])
                .attr("x", function (d) {
                    return d.position.x * d3_wrapper.size;
                })
                .attr("y", function (d) {
                    return d.position.y * d3_wrapper.size;
                });
            this.selectedUnitBuiltSvg.enter()
                .append("image")
                .attr("class", "unit selected")
                .attr("x", function (d) {
                    return d.position.x * d3_wrapper.size;
                })
                .attr("y", function (d) {
                    return d.position.y * d3_wrapper.size;
                })
                .attr("width", d3_wrapper.size * 2)
                .attr("height", d3_wrapper.size * 2)
                .attr("fill", "none")
                .attr("href", function () {
                    return that.iconPath("Icons/button active.png")
                })
                .on("click", function (d) {
                    selectUnitCallback(d);
                })
                .on("contextmenu", function (d) {
                    d3.event.preventDefault();
                    if (d3_wrapper.selectedUnitBuiltSvg) {
                        d3_wrapper.selectedUnitBuiltSvg.remove();
                        selectUnitCallback(d);
                    }
                });
            this.selectedUnitBuiltSvg.exit()
                .remove();
        }
        else if (this.selectedUnitBuiltSvg){
            this.selectedUnitBuiltSvg.remove();
        }
    },

    validPlacement: function(cell) {
        return cell.col >= 0 && cell.col < (d3_wrapper.cols - 1) && cell.row >= 0 && cell.row < (d3_wrapper.rows - 1);
    },

    drawInlineSVG: function drawInlineSVG(callback) {
        if (!d3_wrapper.svg) {
            setTimeout(function () {
                drawInlineSVG(callback)
            }, 40);
            return;
        }
        var canvas = document.createElement("canvas");
        canvas.width = (d3_wrapper.size * d3_wrapper.cols);
        canvas.height = (d3_wrapper.size * d3_wrapper.rows);
        var ctx = canvas.getContext('2d');
        var svg = d3_wrapper.svg.node().cloneNode(true);
        svg.setAttribute("width", (d3_wrapper.size * d3_wrapper.cols) + "px");
        svg.childNodes[0].childNodes.forEach(function (value, index) {
            svg.childNodes[0].childNodes[index].setAttribute("fill", "none");
            svg.childNodes[0].childNodes[index].setAttribute("stroke", "#aaa");
        });
        svg.childNodes[1].childNodes.forEach(function (value, index) {
            svg.childNodes[1].childNodes[index].setAttribute("href", d3_wrapper.getBase64Image(value));
        });
        var svgURL = new XMLSerializer().serializeToString(svg);
        var img = new Image();
        img.onload = function () {
            ctx.drawImage(this, 0, 0);
            callback(canvas.toDataURL());
        };
        img.src = 'data:image/svg+xml; charset=utf8, ' + encodeURIComponent(svgURL);
    },

    getBase64Image: function(imgSvg) {
        // Create an empty canvas element
        var canvas = document.createElement("canvas");
        canvas.width = imgSvg.getAttribute("width");
        canvas.height = imgSvg.getAttribute("height");
        var img = new Image();
        img.src = imgSvg.getAttribute("href");
        // Copy the image contents to the canvas
        var ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0);
        // Get the data-URL formatted image
        // Firefox supports PNG and JPEG. You could check img.src to
        // guess the original format, but be aware the using "image/jpg"
        // will re-encode the image.
        return canvas.toDataURL("image/png");
    }
};

if (typeof define === 'function' && define.amd) {
    define(function () { return d3_wrapper; });
} else if( typeof module !== 'undefined' && module != null ) {
    module.exports = d3_wrapper
}