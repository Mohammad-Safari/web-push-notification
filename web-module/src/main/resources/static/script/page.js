const rbeCounter = 2;
const eventSource = new EventSource("/event/sse/2", {});
const eventSource2 = new EventSource(`/event/rbe?counter=${rbeCounter}`, {});

window.onload = (_) => {
  const eventList = document.getElementById("list");
  eventSource.onopen = (event) => {
    addEntry(event, eventList, `open: ${event.target.url}`);
  };
  eventSource.addEventListener("sse event - mvc", (event) => {
    addEntry(event, eventList, `event: sse event - mvc; mvc: ${event.data}`);
  });
  eventSource.addEventListener("complete", (event) => {
    addEntry(event, eventList, `event: complete; data: ${event.data}`);
    eventSource.close();
  });
  /* ************************************************* */
  const eventList2 = document.getElementById("list2");
  let rbeCounterCopy = rbeCounter;
  eventSource2.onopen = (event) => {
    addEntry(event, eventList2, `open: ${event.target.url}`);
  };
  eventSource2.onmessage = (event) => {
    rbeCounterCopy--;
    addEntry(event, eventList2, `message: ${event.data}`);
    if (rbeCounterCopy === 0) {
      eventSource2.close();
      addEntry(event, eventList2, "connection is closed");
    }
  };
  window.onunload = (_) => {
    eventSource.close();
    eventSource2.close();
  };
};
function addEntry(event, eventList, textFormat) {
  const newElement = document.createElement("li");
  newElement.textContent = textFormat;
  eventList.appendChild(newElement);
}
