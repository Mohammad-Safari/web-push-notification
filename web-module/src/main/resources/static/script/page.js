const eventSource = new EventSource("http://localhost:8080/event/sse", {});

window.onload = (_) => {
  const eventList = document.getElementById("list");
  eventSource.onopen = (event) => {
    const newElement = document.createElement("li");
    newElement.textContent = `open: ${event.data}`;
    eventList.appendChild(newElement);
  };
  eventSource.addEventListener("sse event - mvc", (event) => {
    const newElement = document.createElement("li");
    newElement.textContent = `message: ${event.data}`;
    eventList.appendChild(newElement);
  });
  window.onunload = (_) => {
    eventSource.close();
  };
};
