// Cross-browser safe HTML printing utility
// Tries window.open first; if blocked (returns null) falls back to a hidden iframe
// Returns a Promise that resolves after initiating print
export async function printHtml(html, options = {}) {
  const { mode = 'iframe', closeAfterPrint = true } = options; // mode: 'iframe' | 'popup' | 'auto'

  const tryPopupFirst = mode === 'popup' || mode === 'auto';

  // Optionally try opening a new tab/window first
  if (tryPopupFirst) {
    let win = null;
    try {
      win = window.open('', '_blank', 'noopener,noreferrer');
    } catch (_) {
      win = null;
    }

    if (win && win.document) {
      try {
        win.document.open();
        win.document.write(html);
        win.document.close();

        const doPrint = () => {
          try { win.focus(); } catch (_) {}
          try { win.print(); } catch (_) {}
          if (closeAfterPrint) {
            setTimeout(() => { try { win.close(); } catch (_) {} }, 300);
          }
        };

        if (win.document.readyState === 'complete') {
          setTimeout(doPrint, 150);
        } else {
          win.onload = doPrint;
          setTimeout(doPrint, 500);
        }
        return true;
      } catch (_) {
        // fall through to iframe fallback
      }
    }
    // If popup attempt failed and mode was strictly 'popup', stop here
    if (mode === 'popup') return false;
  }

  // Hidden iframe approach (default) avoids opening new tabs
  return new Promise((resolve) => {
    const iframe = document.createElement('iframe');
    iframe.style.position = 'fixed';
    iframe.style.right = '0';
    iframe.style.bottom = '0';
    iframe.style.width = '0';
    iframe.style.height = '0';
    iframe.style.border = '0';

    const cleanup = () => {
      setTimeout(() => {
        try { document.body.removeChild(iframe); } catch (_) {}
        resolve(true);
      }, 0);
    };

    const onLoad = () => {
      try {
        const iw = iframe.contentWindow || iframe;
        try { iw.focus(); } catch (_) {}
        try { iw.print(); } catch (_) {}
      } finally {
        cleanup();
      }
    };

    // Prefer srcdoc where supported
    if ('srcdoc' in iframe) {
      iframe.srcdoc = html;
      iframe.addEventListener('load', onLoad);
      document.body.appendChild(iframe);
      // Extra timeout fallback
      setTimeout(onLoad, 800);
    } else {
      document.body.appendChild(iframe);
      const doc = iframe.contentWindow ? iframe.contentWindow.document : iframe.document;
      doc.open();
      doc.write(html);
      doc.close();
      // Give the iframe time to layout content before printing
      setTimeout(onLoad, 500);
    }
  });
}
