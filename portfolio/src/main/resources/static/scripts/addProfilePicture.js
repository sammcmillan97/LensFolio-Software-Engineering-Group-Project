let profileImageInput = document.getElementById("img-input");

const MAX_PIXELS = 150;
let excessWidth= 0;
let excessHeight=0;
let maxSize=0;

function calculateSize(width, height) {
    if (width === height) {
        maxSize = height;
    } else {
        if (width > height) {
            maxSize = height;
        } else {
            maxSize = width;
        }
    }
    excessWidth = width - maxSize;
    excessHeight = height - maxSize;
}

function dataURItoBlob(dataURI) {
    // convert base64 to raw binary data held in a string
    // doesn't handle URLEncoded DataURIs - see SO answer #6850276 for code that does this
    const byteString = atob(dataURI.split(',')[1]);

    // separate out the mime component
    const mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

    // write the bytes of the string to an ArrayBuffer
    const ab = new ArrayBuffer(byteString.length);

    // create a view into the buffer
    const ia = new Uint8Array(ab);

    // set the bytes of the buffer to the correct values
    for (let i = 0; i < byteString.length; i++) { 
        ia[i] = byteString.charCodeAt(i);
    }

    // write the ArrayBuffer to a blob, and you're done
    return new Blob([ab], {type: mimeString});
}

profileImageInput.onchange = function() {
    //ensure that an image upload doesn't exceed 5MB


    if (document.getElementById("root").childElementCount !== 0) {
        //delete canvas before new one is created
        let children = document.getElementById("root").childNodes;
        document.getElementById("root").removeChild(children[0]);
    }
    const [file] = profileImageInput.files
    if (file) {
        const blobURL = URL.createObjectURL(file);
        const img = new Image();
        img.src = blobURL;
        img.onerror = function() {
            URL.revokeObjectURL(this.src);
            console.log("Cannot load image");
        }
        img.onload = function () {
            URL.revokeObjectURL(this.src);
            const canvas = document.createElement("canvas");
            canvas.width = MAX_PIXELS;
            canvas.height = MAX_PIXELS;
            calculateSize(img.width, img.height);
            const ctx = canvas.getContext("2d");
            ctx.drawImage(img, excessWidth/2, excessHeight/2, maxSize, maxSize, 0, 0, MAX_PIXELS, MAX_PIXELS);
            document.getElementById("root").appendChild(canvas);

            let blob = dataURItoBlob(canvas.toDataURL("image/png"));
            //set file type
            document.getElementById("fileType").value="png";
            //set bytes
            const reader = new FileReader();
            reader.readAsDataURL(blob);
            reader.onloadend = function() {
                document.getElementById("fileContent").value=reader.result;
            }
        }
    }
};