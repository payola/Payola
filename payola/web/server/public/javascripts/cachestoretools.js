function cacheStoreUpdateButtonOnClick(updateButtonElement, analysisId, evaluationId, uriHash) {
    updateButtonElement.setAttribute('disabled','disabled');
    updateButtonElement.removeAttribute('style');
    document.getElementById('result'+evaluationId).removeAttribute('style');

    var updateIconElement = updateButtonElement.getElementsByTagName('i')[0];
    updateIconElement.setAttribute('class', 'cacheUpdateButton glyphicon');

    new cz.payola.web.client.presenters.entity.cachestore.EmbeddedUpdater(analysisId, evaluationId, uriHash, "updateButton"+uriHash).initialize();
}

function cacheStoreUpdateButtonResetSucc(buttonElementId, evaluationId) {
    var buttonElement = document.getElementById(buttonElementId)
    buttonElement.removeAttribute('disabled');
    var updateIconElement = buttonElement.getElementsByTagName('i')[0];
    updateIconElement.setAttribute('class', 'glyphicon-refresh glyphicon');

    buttonElement.setAttribute('style', 'color:#3c763d;')

    document.getElementById('result'+evaluationId).setAttribute('style', 'color:#3c763d;')
    document.getElementById('resultTime'+evaluationId).setAttribute('style', 'color:#ddd;')
    return true;
}

function cacheStoreUpdateButtonResetErr(elementId) {
    var element = document.getElementById(elementId)
    element.removeAttribute('disabled');
    var updateIconElement = element.getElementsByTagName('i')[0];
    updateIconElement.setAttribute('class', 'glyphicon-refresh glyphicon');

    element.setAttribute('style', 'color:#a94442;')
    return true;
}

function setAnchorDetail(anchorElementId, innerText) {
    var origin = window.location.origin;
    var anchorElement = document.getElementById(anchorElementId);
    anchorElement.innerHTML = origin + innerText;
    anchorElement.setAttribute('href', innerText)
}